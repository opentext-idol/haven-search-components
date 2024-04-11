/*
 * Copyright 2015-2017 Open Text.
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
import com.autonomy.aci.client.util.ActionParameters;
import com.hp.autonomy.aci.content.identifier.reference.Reference;
import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.core.search.StateTokenAndResultCount;
import com.hp.autonomy.searchcomponents.core.search.TypedStateToken;
import com.hp.autonomy.searchcomponents.idol.annotations.IdolService;
import com.hp.autonomy.types.requests.Documents;
import com.hp.autonomy.types.requests.idol.actions.query.QueryActions;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import com.hp.autonomy.types.requests.idol.actions.query.params.SuggestParams;
import com.hp.autonomy.types.requests.qms.actions.query.params.QmsQueryParams;
import com.opentext.idol.types.responses.Hit;
import com.opentext.idol.types.responses.QueryResponseData;
import com.opentext.idol.types.responses.SuggestResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.hp.autonomy.searchcomponents.core.search.DocumentsService.DOCUMENTS_SERVICE_BEAN_NAME;

/**
 * Default Idol implementation of {@link DocumentsService}
 */
@Service(DOCUMENTS_SERVICE_BEAN_NAME)
@IdolService
class IdolDocumentsServiceImpl implements IdolDocumentsService {
    // fake token in a format that IDOL is happy with
    private static final String EMPTY_RESULT_SET_TOKEN = "NULL-0";

    private final HavenSearchAciParameterHandler parameterHandler;
    private final QueryExecutor queryExecutor;
    private final QueryResponseParser queryResponseParser;

    @Autowired
    IdolDocumentsServiceImpl(
        final HavenSearchAciParameterHandler parameterHandler,
        final QueryExecutor queryExecutor,
        final QueryResponseParser queryResponseParser
    ) {
        this.parameterHandler = parameterHandler;
        this.queryExecutor = queryExecutor;
        this.queryResponseParser = queryResponseParser;
    }

    @Override
    public Documents<IdolSearchResult> queryTextIndex(final IdolQueryRequest queryRequest) throws AciErrorException {
        final QueryRequest.QueryType queryType = queryRequest.getQueryType();
        if(!queryExecutor.performQuery(queryType)) {
            return new Documents<>(Collections.emptyList(), 0, null, null, null, null);
        }

        final ActionParameters aciParameters = new ActionParameters(QueryActions.Query.name());

        parameterHandler.addSearchRestrictions(aciParameters, queryRequest.getQueryRestrictions());
        parameterHandler.addUserIdentifiers(aciParameters);
        parameterHandler.addSearchOutputParameters(aciParameters, queryRequest);
        if(queryType != QueryRequest.QueryType.RAW) {
            parameterHandler.addQmsParameters(aciParameters, queryRequest.getQueryRestrictions());
        }

        if (queryRequest.isIntentBasedRanking()) {
            parameterHandler.addIntentBasedRankingParameters(aciParameters);
        }

        if(queryRequest.isAutoCorrect()) {
            aciParameters.add(QueryParams.SpellCheck.name(), true);
        }

        if(queryType == QueryRequest.QueryType.PROMOTIONS) {
            aciParameters.add(QmsQueryParams.Promotions.name(), true);
        }

        final QueryResponseData responseData = queryExecutor.executeQuery(aciParameters, queryType);
        return queryResponseParser.parseQueryResults(
            queryRequest,
            aciParameters,
            responseData,
            parameters -> queryExecutor.executeQuery(parameters, queryType)
        );
    }

    @Override
    public Documents<IdolSearchResult> findSimilar(final IdolSuggestRequest suggestRequest) throws AciErrorException {
        final ActionParameters aciParameters = new ActionParameters(QueryActions.Suggest.name());
        aciParameters.add(SuggestParams.Reference.name(), new Reference(suggestRequest.getReference()));

        parameterHandler.addSearchRestrictions(aciParameters, suggestRequest.getQueryRestrictions());
        parameterHandler.addUserIdentifiers(aciParameters);
        parameterHandler.addSearchOutputParameters(aciParameters, suggestRequest);

        final SuggestResponseData responseData = queryExecutor.executeSuggest(aciParameters, QueryRequest.QueryType.RAW);
        final List<Hit> hits = responseData.getHits();
        return new Documents<>(
            queryResponseParser.parseQueryHits(hits, suggestRequest.getReferenceField()),
            responseData.getTotalhits(), null, null, null, null);
    }

    @Override
    public List<IdolSearchResult> getDocumentContent(final IdolGetContentRequest request) throws AciErrorException {
        final List<IdolSearchResult> results = new ArrayList<>(request.getIndexesAndReferences().size());

        for(final IdolGetContentRequestIndex indexAndReferences : request.getIndexesAndReferences()) {
            // We use Query and not GetContent here so we can use Combine=simple to ensure returned references are unique
            final ActionParameters aciParameters = new ActionParameters(QueryActions.Query.name());
            parameterHandler.addGetDocumentOutputParameters(aciParameters, indexAndReferences, request);

            final QueryResponseData responseData = queryExecutor.executeQuery(aciParameters, QueryRequest.QueryType.RAW);
            final List<Hit> hits = responseData.getHits();
            results.addAll(queryResponseParser.parseQueryHits(hits, request.getReferenceField()));
        }

        return results;
    }

    @Override
    public String getStateToken(
        final IdolQueryRestrictions queryRestrictions,
        final int maxResults,
        final QueryRequest.QueryType queryType,
        final boolean promotions
    ) throws AciErrorException {
        return getStateTokenAndResultCount(queryRestrictions, maxResults, queryType, promotions)
            .getTypedStateToken().getStateToken();
    }

    @Override
    public StateTokenAndResultCount getStateTokenAndResultCount(
        final IdolQueryRestrictions queryRestrictions,
        final int maxResults,
        final QueryRequest.QueryType queryType,
        final boolean promotions
    ) throws AciErrorException {
        final ActionParameters aciParameters = new ActionParameters(QueryActions.Query.name());
        parameterHandler.addSecurityInfo(aciParameters);
        parameterHandler.addStoreStateParameters(aciParameters);
        aciParameters.add(QueryParams.Print.name(), PrintParam.NoResults);
        aciParameters.add(QueryParams.MaxResults.name(), maxResults);

        if(queryType != QueryRequest.QueryType.RAW) {
            parameterHandler.addQmsParameters(aciParameters, queryRestrictions);
        }
        if(promotions) {
            aciParameters.add(QmsQueryParams.Promotions.name(), true);
        }
        parameterHandler.addSearchRestrictions(aciParameters, queryRestrictions);
        parameterHandler.addUserIdentifiers(aciParameters);

        // Unset combine=simple for state token generation
        aciParameters.remove(QueryParams.Combine.name());

        final QueryResponseData responseData = queryExecutor.executeQuery(aciParameters, queryType);
        final String token = responseData.getState() == null
            ? EMPTY_RESULT_SET_TOKEN
            : responseData.getState();
        final TypedStateToken tokenData = new TypedStateToken(
            token,
            promotions
                ? TypedStateToken.StateTokenType.PROMOTIONS
                : TypedStateToken.StateTokenType.QUERY);

        // Now fetch result count with combine=simple
        final ActionParameters resultCountAciParameters = new ActionParameters(QueryActions.Query.name());
        parameterHandler.addSecurityInfo(resultCountAciParameters);
        resultCountAciParameters.add(QueryParams.TotalResults.name(), true);
        resultCountAciParameters.add(QueryParams.Print.name(), PrintParam.NoResults);
        resultCountAciParameters.add(QueryParams.Predict.name(), false);
        if(queryType != QueryRequest.QueryType.RAW) {
            parameterHandler.addQmsParameters(resultCountAciParameters, queryRestrictions);
        }
        parameterHandler.addSearchRestrictions(resultCountAciParameters, queryRestrictions);
        parameterHandler.addUserIdentifiers(resultCountAciParameters);
        final QueryResponseData resultCountResponseData =
            queryExecutor.executeQuery(resultCountAciParameters, queryType);

        return new StateTokenAndResultCount(tokenData, resultCountResponseData.getTotalhits());
    }
}
