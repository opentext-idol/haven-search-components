/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.idol.configuration.AciServiceRetriever;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import com.hp.autonomy.types.idol.responses.GetQueryTagValuesResponseData;
import com.hp.autonomy.types.idol.responses.QueryResponseData;
import com.hp.autonomy.types.idol.responses.SuggestResponseData;
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
