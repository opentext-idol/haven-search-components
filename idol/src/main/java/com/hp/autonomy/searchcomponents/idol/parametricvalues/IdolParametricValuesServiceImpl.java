/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.parametricvalues;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.aci.content.ranges.Range;
import com.hp.autonomy.aci.content.ranges.Ranges;
import com.hp.autonomy.searchcomponents.core.caching.CacheNames;
import com.hp.autonomy.searchcomponents.core.fields.TagNameFactory;
import com.hp.autonomy.searchcomponents.core.parametricvalues.BucketingParams;
import com.hp.autonomy.searchcomponents.core.parametricvalues.BucketingParamsHelper;
import com.hp.autonomy.searchcomponents.core.parametricvalues.DependentParametricField;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.idol.annotations.IdolService;
import com.hp.autonomy.searchcomponents.idol.fields.IdolFieldsRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.fields.IdolFieldsService;
import com.hp.autonomy.searchcomponents.idol.search.HavenSearchAciParameterHandler;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.searchcomponents.idol.search.QueryExecutor;
import com.hp.autonomy.types.idol.responses.FlatField;
import com.hp.autonomy.types.idol.responses.GetQueryTagValuesResponseData;
import com.hp.autonomy.types.idol.responses.RecursiveField;
import com.hp.autonomy.types.idol.responses.TagValue;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagCountInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.RangeInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.TagActions;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.ValueDetails;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import com.hp.autonomy.types.requests.idol.actions.tags.params.GetQueryTagValuesParams;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService.PARAMETRIC_VALUES_SERVICE_BEAN_NAME;

/**
 * Default Idol implementation of {@link ParametricValuesService}
 */
@Service(PARAMETRIC_VALUES_SERVICE_BEAN_NAME)
@IdolService
class IdolParametricValuesServiceImpl implements IdolParametricValuesService {
    static final String VALUE_NODE_NAME = "value";
    static final String VALUES_NODE_NAME = "values";
    static final String VALUE_MIN_NODE_NAME = "valuemin";
    static final String VALUE_MAX_NODE_NAME = "valuemax";
    static final String VALUE_AVERAGE_NODE_NAME = "valueaverage";
    static final String VALUE_SUM_NODE_NAME = "valuesum";

    private final HavenSearchAciParameterHandler parameterHandler;
    private final IdolFieldsService fieldsService;
    private final ObjectFactory<IdolFieldsRequestBuilder> fieldsRequestBuilderFactory;
    private final BucketingParamsHelper bucketingParamsHelper;
    private final TagNameFactory tagNameFactory;
    private final QueryExecutor queryExecutor;

    @SuppressWarnings("ConstructorWithTooManyParameters")
    @Autowired
    IdolParametricValuesServiceImpl(
            final HavenSearchAciParameterHandler parameterHandler,
            final IdolFieldsService fieldsService,
            final ObjectFactory<IdolFieldsRequestBuilder> fieldsRequestBuilderFactory,
            final BucketingParamsHelper bucketingParamsHelper,
            final TagNameFactory tagNameFactory,
            final QueryExecutor queryExecutor
    ) {
        this.parameterHandler = parameterHandler;
        this.fieldsService = fieldsService;
        this.fieldsRequestBuilderFactory = fieldsRequestBuilderFactory;
        this.bucketingParamsHelper = bucketingParamsHelper;
        this.tagNameFactory = tagNameFactory;
        this.queryExecutor = queryExecutor;
    }

    @Override
    public Set<QueryTagInfo> getParametricValues(final IdolParametricRequest parametricRequest) throws AciErrorException {
        final Collection<FieldPath> fieldNames = new HashSet<>();
        fieldNames.addAll(parametricRequest.getFieldNames());

        if (fieldNames.isEmpty()) {
            fieldNames.addAll(lookupFields());
        }

        return fieldNames.isEmpty()
                ? Collections.emptySet()
                : getFlatFields(parametricRequest, fieldNames).stream()
                .map(this::flatFieldToTagInfo)
                .filter(queryTagInfo -> !queryTagInfo.getValues().isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    @Cacheable(CacheNames.PARAMETRIC_VALUES_IN_BUCKETS)
    public List<RangeInfo> getNumericParametricValuesInBuckets(final IdolParametricRequest parametricRequest, final Map<FieldPath, BucketingParams> bucketingParamsPerField) throws AciErrorException {
        if (parametricRequest.getFieldNames().isEmpty()) {
            return Collections.emptyList();
        } else {
            bucketingParamsHelper.validateBucketingParams(parametricRequest, bucketingParamsPerField);

            final Map<FieldPath, List<Double>> boundariesPerField = bucketingParamsPerField.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> bucketingParamsHelper.calculateBoundaries(entry.getValue())));

            final List<Range> ranges = boundariesPerField.entrySet().stream()
                    .map(entry -> new Range(entry.getKey().getNormalisedPath(), ArrayUtils.toPrimitive(entry.getValue().toArray(new Double[entry.getValue().size()]))))
                    .collect(Collectors.toList());

            final IdolParametricRequest bucketingRequest = parametricRequest.toBuilder()
                    .maxValues(null)
                    .start(1)
                    .ranges(ranges)
                    .sort(SortParam.NumberIncreasing)
                    .build();

            return getFlatFields(bucketingRequest, parametricRequest.getFieldNames()).stream()
                    .map(flatFieldToRangeInfo(boundariesPerField))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<DependentParametricField> getDependentParametricValues(final IdolParametricRequest parametricRequest) throws AciErrorException {
        final Collection<FieldPath> fieldNames = new ArrayList<>();
        fieldNames.addAll(parametricRequest.getFieldNames());
        if (fieldNames.isEmpty()) {
            fieldNames.addAll(lookupFields());
        }

        final List<DependentParametricField> results;
        if (fieldNames.isEmpty()) {
            results = Collections.emptyList();
        } else {
            final AciParameters aciParameters = createAciParameters(parametricRequest.getQueryRestrictions(), parametricRequest.isModified());

            parameterHandler.addSecurityInfo(aciParameters);
            aciParameters.add(GetQueryTagValuesParams.DocumentCount.name(), true);
            aciParameters.add(GetQueryTagValuesParams.FieldName.name(), fieldPathsToFieldNamesParam(fieldNames));
            aciParameters.add(GetQueryTagValuesParams.FieldDependence.name(), true);
            aciParameters.add(GetQueryTagValuesParams.FieldDependenceMultiLevel.name(), true);

            final GetQueryTagValuesResponseData responseData = executeAction(parametricRequest, aciParameters);

            results = !responseData.getField().isEmpty() && responseData.getValues() != null
                    ? addDisplayNamesToRecursiveFields(responseData.getValues().getField(), responseData.getField().get(0).getName())
                    : Collections.emptyList();
        }

        return results;
    }

    @Override
    public Map<FieldPath, ValueDetails> getValueDetails(final IdolParametricRequest parametricRequest) throws AciErrorException {
        if (parametricRequest.getFieldNames().isEmpty()) {
            return Collections.emptyMap();
        } else {
            final AciParameters aciParameters = createAciParameters(parametricRequest.getQueryRestrictions(), parametricRequest.isModified());

            parameterHandler.addSecurityInfo(aciParameters);
            aciParameters.add(GetQueryTagValuesParams.MaxValues.name(), 1);
            aciParameters.add(GetQueryTagValuesParams.FieldName.name(), fieldPathsToFieldNamesParam(parametricRequest.getFieldNames()));
            aciParameters.add(GetQueryTagValuesParams.ValueDetails.name(), true);
            aciParameters.add(GetQueryTagValuesParams.Predict.name(), true);

            final GetQueryTagValuesResponseData responseData = executeAction(parametricRequest, aciParameters);
            final Collection<FlatField> fields = responseData.getField();

            final Map<FieldPath, ValueDetails> output = new LinkedHashMap<>();

            for (final FlatField field : fields) {
                final List<JAXBElement<? extends Serializable>> valueElements = field.getValueAndSubvalueOrValues();

                final ValueDetails.Builder builder = new ValueDetails.Builder()
                        .setTotalValues(flatFieldTotalValues(field));

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

                final FieldPath fieldPath = tagNameFactory.getFieldPath(field.getName().get(0));
                output.put(fieldPath, builder.build());
            }

            return output;
        }
    }

    private AciParameters createAciParameters(final IdolQueryRestrictions queryRestrictions, final boolean modified) {
        final AciParameters aciParameters = new AciParameters(TagActions.GetQueryTagValues.name());
        parameterHandler.addSearchRestrictions(aciParameters, queryRestrictions);
        if (modified) {
            parameterHandler.addQmsParameters(aciParameters, queryRestrictions);
        }
        return aciParameters;
    }

    private Collection<FieldPath> lookupFields() {
        return fieldsService.getFields(fieldsRequestBuilderFactory.getObject().fieldType(FieldTypeParam.Parametric).build()).get(FieldTypeParam.Parametric)
                .stream()
                .map(TagName::getId)
                .collect(Collectors.toList());
    }

    private Function<FlatField, RangeInfo> flatFieldToRangeInfo(final Map<FieldPath, List<Double>> boundariesPerField) {
        return flatField -> {
            final TagName tagName = tagNameFactory.buildTagName(flatField.getName().get(0));

            final List<JAXBElement<? extends Serializable>> valueElements = flatField.getValueAndSubvalueOrValues();
            int count = 0;

            final Collection<RangeInfo.Value> values = new LinkedList<>();

            for (final JAXBElement<?> element : valueElements) {
                final String elementLocalName = element.getName().getLocalPart();

                if (VALUE_NODE_NAME.equals(elementLocalName)) {
                    final TagValue tagValue = (TagValue) element.getValue();
                    final String[] rangeValues = tagValue.getValue().split(",");
                    final double min = Double.parseDouble(rangeValues[0]);
                    final double max = Double.parseDouble(rangeValues[1]);
                    values.add(new RangeInfo.Value(min, max, tagValue.getCount()));
                } else if (VALUES_NODE_NAME.equals(elementLocalName)) {
                    count = (Integer) element.getValue();
                }
            }

            final List<Double> boundaries = boundariesPerField.get(tagName.getId());

            // If no documents match the query parameters, GetQueryTagValues does not return any buckets
            if (values.isEmpty()) {
                values.addAll(bucketingParamsHelper.emptyBuckets(boundaries));
            }

            // All buckets have the same size, so just use the value from the first one
            final double bucketSize = boundaries.get(1) - boundaries.get(0);
            return RangeInfo.builder()
                    .id(tagName.getId().getNormalisedPath())
                    .displayName(tagName.getDisplayName())
                    .count(count)
                    .min(boundaries.get(0))
                    .max(boundaries.get(boundaries.size() - 1))
                    .bucketSize(bucketSize)
                    .values(values)
                    .build();
        };
    }

    private QueryTagInfo flatFieldToTagInfo(final FlatField flatField) {
        final String name = flatField.getName().get(0);
        final Set<QueryTagCountInfo> values = flatField.getValueAndSubvalueOrValues().stream()
                .filter(element -> VALUE_NODE_NAME.equals(element.getName().getLocalPart()))
                .map(element -> {
                    final TagValue tagValue = (TagValue) element.getValue();
                    final String value = tagValue.getValue();
                    final String displayValue = tagNameFactory.getTagDisplayValue(name, value);
                    return new QueryTagCountInfo(value, displayValue, tagValue.getCount());
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));

        final TagName tagName = tagNameFactory.buildTagName(name);
        return QueryTagInfo.builder()
                .id(tagName.getId().getNormalisedPath())
                .displayName(tagName.getDisplayName())
                .values(values)
                .totalValues(flatFieldTotalValues(flatField))
                .build();
    }

    private int flatFieldTotalValues(final FlatField flatField) {
        // If no values are returned for a field, IDOL does not return a <total_values> element
        return flatField.getTotalValues() == null ? 0 : flatField.getTotalValues();
    }

    private Collection<FlatField> getFlatFields(final IdolParametricRequest parametricRequest, final Collection<FieldPath> fieldNames) {
        final AciParameters aciParameters = new AciParameters(TagActions.GetQueryTagValues.name());
        parameterHandler.addSearchRestrictions(aciParameters, parametricRequest.getQueryRestrictions());

        if (parametricRequest.isModified()) {
            parameterHandler.addQmsParameters(aciParameters, parametricRequest.getQueryRestrictions());
        }

        parameterHandler.addSecurityInfo(aciParameters);
        aciParameters.add(GetQueryTagValuesParams.DocumentCount.name(), true);
        aciParameters.add(GetQueryTagValuesParams.Start.name(), parametricRequest.getStart());
        aciParameters.add(GetQueryTagValuesParams.MaxValues.name(), parametricRequest.getMaxValues());
        aciParameters.add(GetQueryTagValuesParams.FieldName.name(), fieldPathsToFieldNamesParam(fieldNames));
        aciParameters.add(GetQueryTagValuesParams.Sort.name(), parametricRequest.getSort());
        aciParameters.add(GetQueryTagValuesParams.Ranges.name(), new Ranges(parametricRequest.getRanges()));
        aciParameters.add(GetQueryTagValuesParams.ValueDetails.name(), true);
        aciParameters.add(GetQueryTagValuesParams.TotalValues.name(), true);
        aciParameters.add(GetQueryTagValuesParams.ValueRestriction.name(), String.join(",", parametricRequest.getValueRestrictions()));

        final GetQueryTagValuesResponseData responseData = executeAction(parametricRequest, aciParameters);
        return responseData.getField();
    }

    private String fieldPathsToFieldNamesParam(final Collection<FieldPath> fieldNames) {
        return StringUtils.join(fieldNames.stream().map(FieldPath::getNormalisedPath).toArray(String[]::new), ',');
    }

    private GetQueryTagValuesResponseData executeAction(final ParametricRequest<IdolQueryRestrictions> idolParametricRequest, final AciParameters aciParameters) {
        return queryExecutor.executeGetQueryTagValues(
                aciParameters,
                idolParametricRequest.isModified() ? QueryRequest.QueryType.MODIFIED : QueryRequest.QueryType.RAW
        );
    }

    private List<DependentParametricField> addDisplayNamesToRecursiveFields(final Collection<RecursiveField> recursiveFields, final List<String> fieldNames) {
        return fieldNames.isEmpty()
                ? Collections.emptyList()
                : recursiveFields.stream()
                    .map(recursiveField -> DependentParametricField.builder()
                            .value(recursiveField.getValue())
                            .displayValue(tagNameFactory.getTagDisplayValue(fieldNames.get(0), recursiveField.getValue()))
                            .count(recursiveField.getCount())
                            .subFields(addDisplayNamesToRecursiveFields(recursiveField.getField(), fieldNames.subList(1, fieldNames.size())))
                            .build())
                    .collect(Collectors.toList());
    }
}
