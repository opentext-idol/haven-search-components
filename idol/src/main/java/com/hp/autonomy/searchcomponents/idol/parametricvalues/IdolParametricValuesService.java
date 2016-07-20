/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.parametricvalues;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.aci.content.ranges.Range;
import com.hp.autonomy.aci.content.ranges.Ranges;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.searchcomponents.core.caching.CacheNames;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import com.hp.autonomy.searchcomponents.core.parametricvalues.AbstractParametricValuesService;
import com.hp.autonomy.searchcomponents.core.parametricvalues.BucketingParams;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.idol.fields.IdolFieldsRequest;
import com.hp.autonomy.searchcomponents.idol.search.HavenSearchAciParameterHandler;
import com.hp.autonomy.types.idol.FlatField;
import com.hp.autonomy.types.idol.GetQueryTagValuesResponseData;
import com.hp.autonomy.types.idol.RecursiveField;
import com.hp.autonomy.types.idol.TagValue;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagCountInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.RangeInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.TagActions;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.ValueDetails;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import com.hp.autonomy.types.requests.idol.actions.tags.params.GetQueryTagValuesParams;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@SuppressWarnings("WeakerAccess")
@Service
public class IdolParametricValuesService extends AbstractParametricValuesService<IdolParametricRequest, String, AciErrorException> {
    static final String VALUE_NODE_NAME = "value";
    static final String VALUES_NODE_NAME = "values";
    static final String VALUE_MIN_NODE_NAME = "valuemin";
    static final String VALUE_MAX_NODE_NAME = "valuemax";
    static final String VALUE_AVERAGE_NODE_NAME = "valueaverage";
    static final String VALUE_SUM_NODE_NAME = "valuesum";

    private final HavenSearchAciParameterHandler parameterHandler;
    private final FieldsService<IdolFieldsRequest, AciErrorException> fieldsService;
    private final AciService contentAciService;
    private final Processor<GetQueryTagValuesResponseData> queryTagValuesResponseProcessor;

    @Autowired
    public IdolParametricValuesService(
            final HavenSearchAciParameterHandler parameterHandler,
            final FieldsService<IdolFieldsRequest, AciErrorException> fieldsService,
            final AciService contentAciService,
            final AciResponseJaxbProcessorFactory aciResponseProcessorFactory
    ) {
        this.parameterHandler = parameterHandler;
        this.fieldsService = fieldsService;
        this.contentAciService = contentAciService;
        queryTagValuesResponseProcessor = aciResponseProcessorFactory.createAciResponseProcessor(GetQueryTagValuesResponseData.class);
    }

    @Override
    public Set<QueryTagInfo> getAllParametricValues(final IdolParametricRequest parametricRequest) throws AciErrorException {
        final Collection<String> fieldNames = new HashSet<>();
        fieldNames.addAll(parametricRequest.getFieldNames());
        if (fieldNames.isEmpty()) {
            fieldNames.addAll(lookupFieldIds());
        }

        final Set<QueryTagInfo> results;
        if (fieldNames.isEmpty()) {
            results = Collections.emptySet();
        } else {
            final List<FlatField> fields = getFlatFields(parametricRequest, fieldNames);
            results = new LinkedHashSet<>(fields.size());
            for (final FlatField field : fields) {
                final List<JAXBElement<? extends Serializable>> valueElements = field.getValueAndSubvalueOrValues();
                final LinkedHashSet<QueryTagCountInfo> values = new LinkedHashSet<>(valueElements.size());
                for (final JAXBElement<?> element : valueElements) {
                    if (VALUE_NODE_NAME.equals(element.getName().getLocalPart())) {
                        final TagValue tagValue = (TagValue) element.getValue();
                        values.add(new QueryTagCountInfo(tagValue.getValue(), tagValue.getCount()));
                    }
                }
                if (!values.isEmpty()) {
                    final TagName tagName = new TagName(field.getName().get(0));
                    results.add(new QueryTagInfo(tagName, values));
                }
            }
        }

        return results;
    }

    @Override
    @Cacheable(CacheNames.PARAMETRIC_VALUES_IN_BUCKETS)
    public List<RangeInfo> getNumericParametricValuesInBuckets(final IdolParametricRequest parametricRequest, final Map<String, BucketingParams> bucketingParamsPerField) throws AciErrorException {
        if (parametricRequest.getFieldNames().isEmpty()) {
            return Collections.emptyList();
        } else {
            validateBucketingParams(parametricRequest, bucketingParamsPerField);

            final Map<String, List<Double>> boundariesPerField = new HashMap<>();

            for (final Map.Entry<String, BucketingParams> entry : bucketingParamsPerField.entrySet()) {
                boundariesPerField.put(entry.getKey(), calculateBoundaries(entry.getValue()));
            }

            return queryForRanges(parametricRequest, boundariesPerField);
        }
    }

    @Override
    public List<RecursiveField> getDependentParametricValues(final IdolParametricRequest parametricRequest) throws AciErrorException {
        final Collection<String> fieldNames = new ArrayList<>();
        fieldNames.addAll(parametricRequest.getFieldNames());
        if (fieldNames.isEmpty()) {
            fieldNames.addAll(lookupFieldIds());
        }

        final List<RecursiveField> results;
        if (fieldNames.isEmpty()) {
            results = Collections.emptyList();
        } else {
            final AciParameters aciParameters = createAciParameters(parametricRequest.getQueryRestrictions(), parametricRequest.isModified());

            aciParameters.add(GetQueryTagValuesParams.DocumentCount.name(), true);
            aciParameters.add(GetQueryTagValuesParams.FieldName.name(), StringUtils.join(fieldNames.toArray(), ','));
            aciParameters.add(GetQueryTagValuesParams.FieldDependence.name(), true);
            aciParameters.add(GetQueryTagValuesParams.FieldDependenceMultiLevel.name(), true);


            final GetQueryTagValuesResponseData responseData = contentAciService.executeAction(aciParameters, queryTagValuesResponseProcessor);

            results = responseData.getValues() == null ? Collections.<RecursiveField>emptyList() : responseData.getValues().getField();
        }

        return results;
    }

    @Override
    public Map<TagName, ValueDetails> getValueDetails(final IdolParametricRequest parametricRequest) throws AciErrorException {
        if (parametricRequest.getFieldNames().isEmpty()) {
            return Collections.emptyMap();
        } else {
            final AciParameters aciParameters = createAciParameters(parametricRequest.getQueryRestrictions(), parametricRequest.isModified());

            aciParameters.add(GetQueryTagValuesParams.MaxValues.name(), 1);
            aciParameters.add(GetQueryTagValuesParams.FieldName.name(), StringUtils.join(parametricRequest.getFieldNames(), ','));
            aciParameters.add(GetQueryTagValuesParams.ValueDetails.name(), true);

            final GetQueryTagValuesResponseData responseData = contentAciService.executeAction(aciParameters, queryTagValuesResponseProcessor);
            final Collection<FlatField> fields = responseData.getField();

            final Map<TagName, ValueDetails> output = new LinkedHashMap<>();

            for (final FlatField field : fields) {
                final List<JAXBElement<? extends Serializable>> valueElements = field.getValueAndSubvalueOrValues();

                final ValueDetails.Builder builder = new ValueDetails.Builder()
                        .setTotalValues(field.getTotalValues());

                for (final JAXBElement<?> element : valueElements) {
                    final String elementLocalName = element.getName().getLocalPart();

                    if (VALUE_MIN_NODE_NAME.equals(elementLocalName)) {
                        builder.setMin((Double) element.getValue());
                    } else if (VALUE_MAX_NODE_NAME.equals(elementLocalName)) {
                        builder.setMax((Double) element.getValue());
                    } else if (VALUE_AVERAGE_NODE_NAME.equals(elementLocalName)) {
                        builder.setAverage((Double) element.getValue());
                    } else if (VALUE_SUM_NODE_NAME.equals(elementLocalName)) {
                        builder.setSum((Double) element.getValue());
                    }
                }

                final TagName tagName = new TagName(field.getName().get(0));
                output.put(tagName, builder.build());
            }

            return output;
        }
    }

    private AciParameters createAciParameters(final QueryRestrictions<String> queryRestrictions, final boolean modified) {
        final AciParameters aciParameters = new AciParameters(TagActions.GetQueryTagValues.name());
        parameterHandler.addSearchRestrictions(aciParameters, queryRestrictions);
        if (modified) {
            parameterHandler.addQmsParameters(aciParameters, queryRestrictions);
        }
        return aciParameters;
    }

    private Collection<String> lookupFieldIds() {
        final List<TagName> fields = fieldsService.getFields(new IdolFieldsRequest.Builder().build(), FieldTypeParam.Parametric).get(FieldTypeParam.Parametric);
        final Collection<String> fieldIds = new ArrayList<>(fields.size());
        for (final TagName field : fields) {
            fieldIds.add(field.getId());
        }

        return fieldIds;
    }

    private List<RangeInfo> queryForRanges(final ParametricRequest<String> parametricRequest, final Map<String, List<Double>> boundariesPerField) {
        final List<Range> ranges = new LinkedList<>();

        for (final Map.Entry<String, List<Double>> entry : boundariesPerField.entrySet()) {
            final List<Double> boundaries = entry.getValue();
            ranges.add(new Range(entry.getKey(), ArrayUtils.toPrimitive(boundaries.toArray(new Double[boundaries.size()]))));
        }

        final IdolParametricRequest bucketingRequest = new IdolParametricRequest.Builder()
                .setFieldNames(parametricRequest.getFieldNames())
                .setMaxValues(null)
                .setSort(parametricRequest.getSort())
                .setRanges(ranges)
                .setQueryRestrictions(parametricRequest.getQueryRestrictions())
                .setModified(parametricRequest.isModified())
                .build();

        final List<FlatField> flatFields = getFlatFields(bucketingRequest, parametricRequest.getFieldNames());
        final List<RangeInfo> results = new LinkedList<>();

        for (final FlatField field : flatFields) {
            final TagName tagName = new TagName(field.getName().get(0));

            final List<JAXBElement<? extends Serializable>> valueElements = field.getValueAndSubvalueOrValues();
            int count = 0;

            // Map of bucket min to bucket data
            final Map<Double, RangeInfo.Value> values = new TreeMap<>();

            for (final JAXBElement<?> element : valueElements) {
                final String elementLocalName = element.getName().getLocalPart();

                if (VALUE_NODE_NAME.equals(elementLocalName)) {
                    final TagValue tagValue = (TagValue) element.getValue();
                    final String[] rangeValues = tagValue.getValue().split(",");
                    final double min = Double.parseDouble(rangeValues[0]);
                    final double max = Double.parseDouble(rangeValues[1]);
                    values.put(min, new RangeInfo.Value(tagValue.getCount(), min, max));
                } else if (VALUES_NODE_NAME.equals(elementLocalName)) {
                    count = (Integer) element.getValue();
                }
            }

            final List<Double> boundaries = boundariesPerField.get(tagName.getId());

            // Boundaries includes the min and the max values, so has a minimum size of 2
            for (int i = 0; i < boundaries.size() - 1; i++) {
                final Double min = boundaries.get(i);

                if (!values.containsKey(min)) {
                    values.put(min, new RangeInfo.Value(0, min, boundaries.get(i + 1)));
                }
            }

            // All buckets have the same size, so just use the value from the first one
            final double bucketSize = boundaries.get(1) - boundaries.get(0);
            results.add(new RangeInfo(tagName, count, boundaries.get(0), boundaries.get(boundaries.size() - 1), bucketSize, new ArrayList<>(values.values())));
        }

        return results;
    }

    private List<FlatField> getFlatFields(final ParametricRequest<String> parametricRequest, final Collection<String> fieldNames) {
        final AciParameters aciParameters = new AciParameters(TagActions.GetQueryTagValues.name());
        parameterHandler.addSearchRestrictions(aciParameters, parametricRequest.getQueryRestrictions());

        if (parametricRequest.isModified()) {
            parameterHandler.addQmsParameters(aciParameters, parametricRequest.getQueryRestrictions());
        }

        aciParameters.add(GetQueryTagValuesParams.DocumentCount.name(), true);
        aciParameters.add(GetQueryTagValuesParams.MaxValues.name(), parametricRequest.getMaxValues());
        aciParameters.add(GetQueryTagValuesParams.FieldName.name(), StringUtils.join(fieldNames.toArray(), ','));
        aciParameters.add(GetQueryTagValuesParams.Sort.name(), parametricRequest.getSort());
        aciParameters.add(GetQueryTagValuesParams.Ranges.name(), new Ranges(parametricRequest.getRanges()));
        aciParameters.add(GetQueryTagValuesParams.ValueDetails.name(), true);

        final GetQueryTagValuesResponseData responseData = contentAciService.executeAction(aciParameters, queryTagValuesResponseProcessor);
        return responseData.getField();
    }
}
