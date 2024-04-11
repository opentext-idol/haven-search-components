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
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsService;
import com.hp.autonomy.searchcomponents.idol.annotations.IdolService;
import com.hp.autonomy.types.requests.idol.actions.query.QueryActions;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import com.opentext.idol.types.responses.QsElement;
import com.opentext.idol.types.responses.QueryResponseData;
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
        final ActionParameters parameters = new ActionParameters(QueryActions.Query.name());
        parameterHandler.addSecurityInfo(parameters);
        parameterHandler.addSearchRestrictions(parameters, relatedConceptsRequest.getQueryRestrictions());
        parameterHandler.addUserIdentifiers(parameters);
        parameters.add(QueryParams.MaxResults.name(), relatedConceptsRequest.getMaxResults());
        parameters.add(QueryParams.Print.name(), PrintParam.NoResults);
        parameters.add(QueryParams.QuerySummary.name(), true);
        parameters.add(QueryParams.QuerySummaryLength.name(), relatedConceptsRequest.getQuerySummaryLength());

        final QueryResponseData responseData = queryExecutor.executeQuery(parameters, relatedConceptsRequest.getQueryType());
        return responseData.getQs() == null
            ? Collections.emptyList()
            : responseData.getQs().getElement();
    }
}
