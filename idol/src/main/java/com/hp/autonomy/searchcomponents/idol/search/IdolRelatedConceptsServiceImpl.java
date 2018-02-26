/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsService;
import com.hp.autonomy.searchcomponents.idol.annotations.IdolService;
import com.hp.autonomy.types.idol.responses.QsElement;
import com.hp.autonomy.types.idol.responses.QueryResponseData;
import com.hp.autonomy.types.requests.idol.actions.query.QueryActions;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.hp.autonomy.searchcomponents.core.search.RelatedConceptsService.RELATED_CONCEPTS_SERVICE_BEAN_NAME;

/**
 * Default Idol implementation of {@link RelatedConceptsService}
 */
@SuppressWarnings("WeakerAccess")
@Service(RELATED_CONCEPTS_SERVICE_BEAN_NAME)
@IdolService
class IdolRelatedConceptsServiceImpl implements IdolRelatedConceptsService {
    private final HavenSearchAciParameterHandler parameterHandler;
    private final QueryExecutor queryExecutor;

    @Autowired
    IdolRelatedConceptsServiceImpl(final HavenSearchAciParameterHandler parameterHandler, final QueryExecutor queryExecutor) {
        this.parameterHandler = parameterHandler;
        this.queryExecutor = queryExecutor;
    }

    @Override
    public List<QsElement> findRelatedConcepts(final IdolRelatedConceptsRequest relatedConceptsRequest) throws AciErrorException {
        final AciParameters parameters = new AciParameters(QueryActions.Query.name());
        parameterHandler.addSecurityInfo(parameters);
        parameterHandler.addSearchRestrictions(parameters, relatedConceptsRequest.getQueryRestrictions());
        parameterHandler.addUserIdentifiers(parameters);
        parameters.add(QueryParams.MaxResults.name(), relatedConceptsRequest.getMaxResults());
        parameters.add(QueryParams.Print.name(), PrintParam.NoResults);
        parameters.add(QueryParams.QuerySummary.name(), true);
        parameters.add(QueryParams.QuerySummaryLength.name(), relatedConceptsRequest.getQuerySummaryLength());

        final QueryResponseData responseData = queryExecutor.executeQuery(parameters, QueryRequest.QueryType.RAW);
        return responseData.getQs() == null
            ? Collections.emptyList()
            : responseData.getQs().getElement();
    }
}
