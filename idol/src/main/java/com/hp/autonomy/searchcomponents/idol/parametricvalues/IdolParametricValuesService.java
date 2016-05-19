/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.parametricvalues;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.google.common.collect.ImmutableList;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.searchcomponents.idol.fields.IdolFieldsRequest;
import com.hp.autonomy.searchcomponents.idol.search.HavenSearchAciParameterHandler;
import com.hp.autonomy.types.idol.FlatField;
import com.hp.autonomy.types.idol.GetQueryTagValuesResponseData;
import com.hp.autonomy.types.idol.RecursiveField;
import com.hp.autonomy.types.idol.TagValue;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagCountInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.TagActions;
import com.hp.autonomy.types.requests.idol.actions.tags.TagResponse;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import com.hp.autonomy.types.requests.idol.actions.tags.params.GetQueryTagValuesParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
@Service
public class IdolParametricValuesService implements ParametricValuesService<IdolParametricRequest, String, AciErrorException> {
    private static final String VALUE_NODE_NAME = "value";
    private static final Pattern CSV_SEPARATOR_PATTERN = Pattern.compile(",\\s*");

    private final HavenSearchAciParameterHandler parameterHandler;
    private final FieldsService<IdolFieldsRequest, AciErrorException> fieldsService;
    private final AciService contentAciService;
    private final Processor<GetQueryTagValuesResponseData> queryTagValuesResponseProcessor;

    @Autowired
    public IdolParametricValuesService(final HavenSearchAciParameterHandler parameterHandler, final FieldsService<IdolFieldsRequest, AciErrorException> fieldsService, final AciService contentAciService, final AciResponseJaxbProcessorFactory aciResponseProcessorFactory) {
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
            fieldNames.addAll(fieldsService.getParametricFields(new IdolFieldsRequest.Builder().build()));
        }

        final Set<QueryTagInfo> results;
        if (fieldNames.isEmpty()) {
            results = Collections.emptySet();
        } else {
            final List<FlatField> fields = getFlatFields(parametricRequest, fieldNames);
            results = new LinkedHashSet<>(fields.size());
            for (final FlatField field : fields) {
                final List<JAXBElement<? extends Serializable>> valueElements = field.getValueOrSubvalueOrValues();
                final LinkedHashSet<QueryTagCountInfo> values = new LinkedHashSet<>(valueElements.size());
                for (final JAXBElement<?> element : valueElements) {
                    if (VALUE_NODE_NAME.equals(element.getName().getLocalPart())) {
                        final TagValue tagValue = (TagValue) element.getValue();
                        values.add(new QueryTagCountInfo(tagValue.getValue(), tagValue.getCount()));
                    }
                }
                final String fieldName = getFieldNameFromPath(field.getName().get(0));
                if (!values.isEmpty()) {
                    results.add(new QueryTagInfo(fieldName, values));
                }
            }
        }

        return results;
    }

    @Override
    public Set<QueryTagInfo> getNumericParametricValues(final IdolParametricRequest parametricRequest) throws AciErrorException {
        final Collection<String> fieldNames = new HashSet<>();
        fieldNames.addAll(parametricRequest.getFieldNames());
        if (fieldNames.isEmpty()) {
            final IdolFieldsRequest fieldsRequest = new IdolFieldsRequest.Builder().build();
            final TagResponse response = fieldsService.getFields(fieldsRequest, ImmutableList.of(FieldTypeParam.Numeric.name(), FieldTypeParam.Parametric.name()));
            if (response.getParametricTypeFields() != null && response.getNumericTypeFields() != null) {
                fieldNames.addAll(response.getParametricTypeFields());
                fieldNames.retainAll(response.getNumericTypeFields());
            }
        }

        final Set<QueryTagInfo> results;
        if (fieldNames.isEmpty()) {
            results = Collections.emptySet();
        } else {
            final List<FlatField> fields = getFlatFields(parametricRequest, fieldNames);
            results = new LinkedHashSet<>(fields.size());
            for (final FlatField field : fields) {
                final Map<Double, Integer> values = parseNumericFieldValuesToMap(field);
                final String fieldName = getFieldNameFromPath(field.getName().get(0));
                if (!values.isEmpty()) {
                    final Set<QueryTagCountInfo> countInfo = new LinkedHashSet<>(values.size());
                    for (final Map.Entry<Double, Integer> entry : values.entrySet()) {
                        countInfo.add(new QueryTagCountInfo(entry.getKey().toString(), entry.getValue()));
                    }

                    results.add(new QueryTagInfo(fieldName, countInfo));
                }
            }
        }

        return results;
    }

    @Override
    public List<RecursiveField> getDependentParametricValues(final IdolParametricRequest parametricRequest) throws AciErrorException {
        final Collection<String> fieldNames = new ArrayList<>();
        fieldNames.addAll(parametricRequest.getFieldNames());
        if (fieldNames.isEmpty()) {
            fieldNames.addAll(fieldsService.getParametricFields(new IdolFieldsRequest.Builder().build()));
        }

        final List<RecursiveField> results;
        if (fieldNames.isEmpty()) {
            results = Collections.emptyList();
        } else {
            final AciParameters aciParameters = new AciParameters(TagActions.GetQueryTagValues.name());
            parameterHandler.addSearchRestrictions(aciParameters, parametricRequest.getQueryRestrictions());

            if (parametricRequest.isModified()) {
                parameterHandler.addQmsParameters(aciParameters, parametricRequest.getQueryRestrictions());
            }

            aciParameters.add(GetQueryTagValuesParams.DocumentCount.name(), true);
            aciParameters.add(GetQueryTagValuesParams.FieldName.name(), StringUtils.join(fieldNames.toArray(), ','));
            aciParameters.add(GetQueryTagValuesParams.FieldDependence.name(), true);
            aciParameters.add(GetQueryTagValuesParams.FieldDependenceMultiLevel.name(), true);


            final GetQueryTagValuesResponseData responseData = contentAciService.executeAction(aciParameters, queryTagValuesResponseProcessor);

            results = responseData.getValues() == null ? Collections.<RecursiveField>emptyList() : responseData.getValues().getField();
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

        final GetQueryTagValuesResponseData responseData = contentAciService.executeAction(aciParameters, queryTagValuesResponseProcessor);
        return responseData.getField();
    }

    private Map<Double, Integer> parseNumericFieldValuesToMap(final FlatField field) {
        final List<JAXBElement<? extends Serializable>> valueElements = field.getValueOrSubvalueOrValues();
        final Map<Double, Integer> values = new TreeMap<>();
        for (final JAXBElement<?> element : valueElements) {
            if (VALUE_NODE_NAME.equals(element.getName().getLocalPart())) {
                final TagValue tagValue = (TagValue) element.getValue();
                // Numeric fields are permitted to contain a csv of numbers as a value
                final String[] csv = CSV_SEPARATOR_PATTERN.split(tagValue.getValue());
                final Integer count = tagValue.getCount();
                for (final String value : csv) {
                    final double numericValue = Double.parseDouble(value);
                    if (values.containsKey(numericValue)) {
                        values.put(numericValue, values.get(numericValue) + count);
                    } else {
                        values.put(numericValue, count);
                    }
                }
            }
        }
        return values;
    }

    private String getFieldNameFromPath(final String value) {
        return value.contains("/") ? value.substring(value.lastIndexOf('/') + 1) : value;
    }
}
