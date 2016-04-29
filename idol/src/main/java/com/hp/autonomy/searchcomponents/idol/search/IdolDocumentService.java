/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.aci.content.identifier.reference.Reference;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.searchcomponents.core.search.*;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.types.idol.Hit;
import com.hp.autonomy.types.idol.QueryResponseData;
import com.hp.autonomy.types.idol.SuggestResponseData;
import com.hp.autonomy.types.requests.Documents;
import com.hp.autonomy.types.requests.idol.actions.query.QueryActions;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import com.hp.autonomy.types.requests.idol.actions.query.params.SuggestParams;
import com.hp.autonomy.types.requests.qms.actions.query.params.QmsQueryParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class IdolDocumentService implements DocumentsService<String, IdolSearchResult, AciErrorException> {
    protected final ConfigService<? extends IdolSearchCapable> configService;
    protected final HavenSearchAciParameterHandler parameterHandler;
    protected final QueryResponseParser queryResponseParser;
    protected final AciService contentAciService;
    protected final AciService qmsAciService;
    protected final Processor<QueryResponseData> queryResponseProcessor;
    protected final Processor<SuggestResponseData> suggestResponseProcessor;

    @SuppressWarnings("ConstructorWithTooManyParameters")
    public IdolDocumentService(
            final ConfigService<? extends IdolSearchCapable> configService,
            final HavenSearchAciParameterHandler parameterHandler,
            final QueryResponseParser queryResponseParser,
            final AciService contentAciService,
            final AciService qmsAciService,
            final AciResponseJaxbProcessorFactory aciResponseProcessorFactory) {
        this.configService = configService;
        this.parameterHandler = parameterHandler;
        this.queryResponseParser = queryResponseParser;
        this.contentAciService = contentAciService;
        this.qmsAciService = qmsAciService;

        queryResponseProcessor = aciResponseProcessorFactory.createAciResponseProcessor(QueryResponseData.class);
        suggestResponseProcessor = aciResponseProcessorFactory.createAciResponseProcessor(SuggestResponseData.class);
    }

    @Override
    public Documents<IdolSearchResult> queryTextIndex(final SearchRequest<String> searchRequest) throws AciErrorException {
        final SearchRequest.QueryType queryType = searchRequest.getQueryType();
        final boolean useQms = qmsEnabled() && queryType != SearchRequest.QueryType.RAW;
        return queryTextIndex(useQms ? qmsAciService : contentAciService, searchRequest, queryType == SearchRequest.QueryType.PROMOTIONS);
    }

    private boolean qmsEnabled() {
        return configService.getConfig().getQueryManipulation().isEnabled();
    }

    @Override
    public Documents<IdolSearchResult> queryTextIndexForPromotions(final SearchRequest<String> searchRequest) throws AciErrorException {
        return qmsEnabled() ? queryTextIndex(qmsAciService, searchRequest, true) : new Documents<>(Collections.<IdolSearchResult>emptyList(), 0, null, null, null, null);
    }

    @Override
    public Documents<IdolSearchResult> findSimilar(final SuggestRequest<String> suggestRequest) throws AciErrorException {
        final AciParameters aciParameters = new AciParameters(QueryActions.Suggest.name());
        aciParameters.add(SuggestParams.Reference.name(), new Reference(suggestRequest.getReference()));

        parameterHandler.addSearchRestrictions(aciParameters, suggestRequest.getQueryRestrictions());
        parameterHandler.addSearchOutputParameters(aciParameters, suggestRequest);

        final SuggestResponseData responseData = contentAciService.executeAction(aciParameters, suggestResponseProcessor);
        final List<Hit> hits = responseData.getHits();
        return new Documents<>(queryResponseParser.parseQueryHits(hits), responseData.getTotalhits(), null, null, null, null);
    }

    @Override
    public List<IdolSearchResult> getDocumentContent(final GetContentRequest<String> request) throws AciErrorException {
        final List<IdolSearchResult> results = new ArrayList<>(request.getIndexesAndReferences().size());

        for (final GetContentRequestIndex<String> indexAndReferences : request.getIndexesAndReferences()) {

            // We use Query and not GetContent here so we can use Combine=simple to ensure returned references are unique
            final AciParameters aciParameters = new AciParameters(QueryActions.Query.name());
            parameterHandler.addGetDocumentOutputParameters(aciParameters, indexAndReferences, PrintParam.fromValue(request.getPrint()));

            final QueryResponseData responseData = contentAciService.executeAction(aciParameters, queryResponseProcessor);
            final List<Hit> hits = responseData.getHits();
            results.addAll(queryResponseParser.parseQueryHits(hits));
        }

        return results;
    }

    @Override
    public String getStateToken(final QueryRestrictions<String> queryRestrictions, final int maxResults) throws AciErrorException {
        return getStateTokenAndResultCount(queryRestrictions, maxResults).getStateToken();
    }

    @Override
    public StateTokenAndResultCount getStateTokenAndResultCount(final QueryRestrictions<String> queryRestrictions, final int maxResults) throws AciErrorException {
        final AciParameters aciParameters = new AciParameters(QueryActions.Query.name());
        aciParameters.add(QueryParams.StoreState.name(), true);
        aciParameters.add(QueryParams.StoredStateTokenLifetime.name(), -1);  // negative value means no expiry (DAH)
        aciParameters.add(QueryParams.Print.name(), PrintParam.NoResults);
        aciParameters.add(QueryParams.MaxResults.name(), maxResults);

        // No promotion or QMS related parameters added; at the time of writing, QMS does not fully support stored state
        parameterHandler.addSearchRestrictions(aciParameters, queryRestrictions);

        // Unset combine=simple for state token generation
        aciParameters.remove(QueryParams.Combine.name());

        final QueryResponseData responseData = contentAciService.executeAction(aciParameters, queryResponseProcessor);

        // Now fetch result count with combine=simple
        final AciParameters resultCountAciParameters = new AciParameters(QueryActions.Query.name());
        resultCountAciParameters.add(QueryParams.TotalResults.name(), true);
        resultCountAciParameters.add(QueryParams.Print.name(), PrintParam.NoResults);
        resultCountAciParameters.add(QueryParams.Predict.name(), false);
        parameterHandler.addSearchRestrictions(resultCountAciParameters, queryRestrictions);
        final QueryResponseData resultCountResponseData = contentAciService.executeAction(resultCountAciParameters, queryResponseProcessor);

        return new StateTokenAndResultCount(responseData.getState(), resultCountResponseData.getTotalhits());
    }

    private Documents<IdolSearchResult> queryTextIndex(final AciService aciService, final SearchRequest<String> searchRequest, final boolean promotions) {
        final AciParameters aciParameters = new AciParameters(QueryActions.Query.name());

        parameterHandler.addSearchRestrictions(aciParameters, searchRequest.getQueryRestrictions());
        parameterHandler.addSearchOutputParameters(aciParameters, searchRequest);
        if (searchRequest.getQueryType() != SearchRequest.QueryType.RAW) {
            parameterHandler.addQmsParameters(aciParameters, searchRequest.getQueryRestrictions());
        }

        if (searchRequest.isAutoCorrect()) {
            aciParameters.add(QueryParams.SpellCheck.name(), true);
        }

        if (promotions) {
            aciParameters.add(QmsQueryParams.Promotions.name(), true);
        }

        final QueryResponseData responseData = executeQuery(aciService, aciParameters);

        return queryResponseParser.parseQueryResults(searchRequest, aciParameters, responseData, new QueryExecutor() {
            @Override
            public QueryResponseData execute(final AciParameters parameters) {
                return executeQuery(aciService, parameters);
            }
        });
    }

    @SuppressWarnings("TypeMayBeWeakened")
    protected QueryResponseData executeQuery(final AciService aciService, final AciParameters aciParameters) {
        return aciService.executeAction(aciParameters, queryResponseProcessor);
    }

    //TODO replace with method reference or similar once we upgrade to java 8
    public interface QueryExecutor {
        QueryResponseData execute(final AciParameters parameters);
    }
}
