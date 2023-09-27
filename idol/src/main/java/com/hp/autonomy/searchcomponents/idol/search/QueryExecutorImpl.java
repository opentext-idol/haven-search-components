/*
 * Copyright 2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.idol.configuration.AciServiceRetriever;
import com.opentext.idol.types.marshalling.ProcessorFactory;
import com.opentext.idol.types.responses.GetQueryTagValuesResponseData;
import com.opentext.idol.types.responses.QueryResponseData;
import com.opentext.idol.types.responses.SuggestResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.hp.autonomy.searchcomponents.idol.search.QueryExecutor.QUERY_EXECUTOR_BEAN_NAME;

/**
 * Default implementation of {@link QueryExecutor}
 */
@Component(QUERY_EXECUTOR_BEAN_NAME)
class QueryExecutorImpl implements QueryExecutor {
    private final AciServiceRetriever aciServiceRetriever;
    private final Processor<QueryResponseData> queryResponseProcessor;
    private final Processor<SuggestResponseData> suggestResponseProcessor;
    private final Processor<GetQueryTagValuesResponseData> queryTagValuesResponseProcessor;

    @Autowired
    public QueryExecutorImpl(final AciServiceRetriever aciServiceRetriever,
                             final ProcessorFactory processorFactory) {
        this.aciServiceRetriever = aciServiceRetriever;

        queryResponseProcessor = processorFactory.getResponseDataProcessor(QueryResponseData.class);
        suggestResponseProcessor = processorFactory.getResponseDataProcessor(SuggestResponseData.class);
        queryTagValuesResponseProcessor = processorFactory.getResponseDataProcessor(GetQueryTagValuesResponseData.class);
    }

    @Override
    public boolean performQuery(final QueryRequest.QueryType queryType) {
        return queryType != QueryRequest.QueryType.PROMOTIONS || aciServiceRetriever.qmsEnabled();
    }

    @Override
    public QueryResponseData executeQuery(final AciParameters aciParameters, final QueryRequest.QueryType queryType) {
        final AciService aciService = aciServiceRetriever.getAciService(queryType);
        return aciService.executeAction(aciParameters, queryResponseProcessor);
    }

    @Override
    public SuggestResponseData executeSuggest(final AciParameters aciParameters, final QueryRequest.QueryType queryType) {
        final AciService aciService = aciServiceRetriever.getAciService(queryType);
        return aciService.executeAction(aciParameters, suggestResponseProcessor);
    }

    @Override
    public GetQueryTagValuesResponseData executeGetQueryTagValues(final AciParameters aciParameters, final QueryRequest.QueryType queryType) throws AciErrorException {
        final AciService aciService = aciServiceRetriever.getAciService(queryType);
        return aciService.executeAction(aciParameters, queryTagValuesResponseProcessor);
    }
}
