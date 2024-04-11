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

import com.autonomy.aci.client.util.ActionParameters;
import com.hp.autonomy.searchcomponents.idol.view.IdolViewRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Provides default methods for any custom implementation of {@link HavenSearchAciParameterHandler}
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public abstract class HavenSearchAciParameterHandlerProxy implements HavenSearchAciParameterHandler {
    @Autowired
    @Qualifier(PARAMETER_HANDLER_BEAN_NAME)
    protected HavenSearchAciParameterHandler parameterHandler;

    @Override
    public void addSearchRestrictions(final ActionParameters aciParameters, final IdolQueryRestrictions queryRestrictions) {
        parameterHandler.addSearchRestrictions(aciParameters, queryRestrictions);
    }

    @Override
    public void addSearchOutputParameters(final ActionParameters aciParameters, final IdolSearchRequest aciSearchRequest) {
        parameterHandler.addSearchOutputParameters(aciParameters, aciSearchRequest);
    }

    @Override
    public void addGetDocumentOutputParameters(final ActionParameters aciParameters, final IdolGetContentRequestIndex getContentRequestIndex, final IdolGetContentRequest request) {
        parameterHandler.addGetDocumentOutputParameters(aciParameters, getContentRequestIndex, request);
    }

    @Override
    public void addGetContentOutputParameters(final ActionParameters aciParameters, final String database, final String documentReference, final String referenceField) {
        parameterHandler.addGetContentOutputParameters(aciParameters, database, documentReference, referenceField);
    }

    @Override
    public void addLanguageRestriction(final ActionParameters aciParameters, final IdolQueryRestrictions queryRestrictions) {
        parameterHandler.addLanguageRestriction(aciParameters, queryRestrictions);
    }

    @Override
    public void addQmsParameters(final ActionParameters aciParameters, final IdolQueryRestrictions queryRestrictions) {
        parameterHandler.addQmsParameters(aciParameters, queryRestrictions);
    }

    @Override
    public void addIntentBasedRankingParameters(final ActionParameters aciParameters) {
        parameterHandler.addIntentBasedRankingParameters(aciParameters);
    }

    @Override
    public String getSecurityInfo() {
        return parameterHandler.getSecurityInfo();
    }

    @Override
    public void addSecurityInfo(final ActionParameters aciParameters) {
        parameterHandler.addSecurityInfo(aciParameters);
    }

    @Override
    public void addUserIdentifiers(final ActionParameters aciParameters) {
        parameterHandler.addUserIdentifiers(aciParameters);
    }

    @Override
    public void addStoreStateParameters(final ActionParameters aciParameters) {
        parameterHandler.addStoreStateParameters(aciParameters);
    }

    @Override
    public void addViewParameters(final ActionParameters aciParameters, final String reference, final IdolViewRequest viewRequest) {
        parameterHandler.addViewParameters(aciParameters, reference, viewRequest);
    }
}
