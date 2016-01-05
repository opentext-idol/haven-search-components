/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.idol.parametricvalues;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.aci.content.database.Databases;
import com.hp.autonomy.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.types.idol.FlatField;
import com.hp.autonomy.types.idol.GetQueryTagValuesResponseData;
import com.hp.autonomy.types.idol.GetTagNamesResponseData;
import com.hp.autonomy.types.idol.TagValue;
import com.hp.autonomy.types.requests.idol.actions.query.params.CombineParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagCountInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.TagActions;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import com.hp.autonomy.types.requests.idol.actions.tags.params.GetQueryTagValuesParams;
import com.hp.autonomy.types.requests.idol.actions.tags.params.GetTagNamesParams;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;

import javax.xml.bind.JAXBElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class IdolParametricValuesService implements ParametricValuesService<IdolParametricRequest, String, AciErrorException> {
    private static final String IDOL_DATE_PARAMETER_FORMAT = "HH:mm:ss dd/MM/yyyy";
    private static final String VALUE_NODE_NAME = "value";
    private static final int MAX_VALUES = 10;

    private final AciService contentAciService;
    private final Processor<GetTagNamesResponseData> tagNamesResponseProcessor;
    private final Processor<GetQueryTagValuesResponseData> queryTagValuesResponseProcessor;

    public IdolParametricValuesService(final AciService contentAciService, final AciResponseJaxbProcessorFactory aciResponseProcessorFactory) {
        this.contentAciService = contentAciService;
        tagNamesResponseProcessor = aciResponseProcessorFactory.createAciResponseProcessor(GetTagNamesResponseData.class);
        queryTagValuesResponseProcessor = aciResponseProcessorFactory.createAciResponseProcessor(GetQueryTagValuesResponseData.class);
    }

    @Override
    public Set<QueryTagInfo> getAllParametricValues(final IdolParametricRequest idolParametricRequest) throws AciErrorException {
        final Collection<String> fieldNames = new HashSet<>();
        fieldNames.addAll(idolParametricRequest.getFieldNames());
        if (fieldNames.isEmpty()) {
            fieldNames.addAll(getTagNames());
        }

        final Set<QueryTagInfo> results;
        if (fieldNames.isEmpty()) {
            results = Collections.emptySet();
        } else {
            final AciParameters aciParameters = new AciParameters(TagActions.GetQueryTagValues.name());
            aciParameters.add(QueryParams.Combine.name(), CombineParam.Simple);
            aciParameters.add(QueryParams.Text.name(), idolParametricRequest.getQueryText());
            aciParameters.add(QueryParams.FieldText.name(), idolParametricRequest.getFieldText());
            aciParameters.add(QueryParams.DatabaseMatch.name(), new Databases(idolParametricRequest.getDatabases()));
            aciParameters.add(QueryParams.MinDate.name(), formatDate(idolParametricRequest.getMinDate()));
            aciParameters.add(QueryParams.MaxDate.name(), formatDate(idolParametricRequest.getMaxDate()));
            aciParameters.add(QueryParams.AnyLanguage.name(), true);
            aciParameters.add(GetQueryTagValuesParams.DocumentCount.name(), true);
            aciParameters.add(GetQueryTagValuesParams.MaxValues.name(), MAX_VALUES);
            aciParameters.add(GetQueryTagValuesParams.FieldName.name(), StringUtils.join(fieldNames.toArray(), ','));
            aciParameters.add(GetQueryTagValuesParams.Sort.name(), SortParam.DocumentCount.name());

            final GetQueryTagValuesResponseData responseData = contentAciService.executeAction(aciParameters, queryTagValuesResponseProcessor);
            final List<FlatField> fields = responseData.getField();
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

    private String formatDate(final ReadableInstant date) {
        return date == null ? null : DateTimeFormat.forPattern(IDOL_DATE_PARAMETER_FORMAT).print(date);
    }

    private Collection<String> getTagNames() {
        final AciParameters aciParameters = new AciParameters(TagActions.GetTagNames.name());
        aciParameters.add(GetTagNamesParams.FieldType.name(), FieldTypeParam.Parametric);

        final GetTagNamesResponseData responseData = contentAciService.executeAction(aciParameters, tagNamesResponseProcessor);

        final List<GetTagNamesResponseData.Name> names = responseData.getName();
        final Collection<String> tagNames = new ArrayList<>(names.size());
        for (final GetTagNamesResponseData.Name name : names) {
            final String value = name.getValue();
            tagNames.add(getFieldNameFromPath(value));
        }

        return tagNames;
    }

    private String getFieldNameFromPath(final String value) {
        return value.contains("/") ? value.substring(value.lastIndexOf('/') + 1) : value;
    }
}
