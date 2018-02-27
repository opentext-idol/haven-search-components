/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.idol.view.IdolViewRequest;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
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
    public void addSearchRestrictions(final AciParameters aciParameters, final IdolQueryRestrictions queryRestrictions) {
        parameterHandler.addSearchRestrictions(aciParameters, queryRestrictions);
    }

    @Override
    public void addSearchOutputParameters(final AciParameters aciParameters, final IdolSearchRequest aciSearchRequest) {
        parameterHandler.addSearchOutputParameters(aciParameters, aciSearchRequest);
    }

    @Override
    public void addGetDocumentOutputParameters(final AciParameters aciParameters, final IdolGetContentRequestIndex getContentRequestIndex, final PrintParam printParam) {
        parameterHandler.addGetDocumentOutputParameters(aciParameters, getContentRequestIndex, printParam);
    }

    @Override
    public void addGetContentOutputParameters(final AciParameters aciParameters, final String database, final String documentReference, final String referenceField) {
        parameterHandler.addGetContentOutputParameters(aciParameters, database, documentReference, referenceField);
    }

    @Override
    public void addLanguageRestriction(final AciParameters aciParameters, final IdolQueryRestrictions queryRestrictions) {
        parameterHandler.addLanguageRestriction(aciParameters, queryRestrictions);
    }

    @Override
    public void addQmsParameters(final AciParameters aciParameters, final IdolQueryRestrictions queryRestrictions) {
        parameterHandler.addQmsParameters(aciParameters, queryRestrictions);
    }

    @Override
    public void addSecurityInfo(final AciParameters aciParameters) {
        parameterHandler.addSecurityInfo(aciParameters);
    }

    @Override
    public void addUserIdentifiers(final AciParameters aciParameters) {
        parameterHandler.addUserIdentifiers(aciParameters);
    }

    @Override
    public void addStoreStateParameters(final AciParameters aciParameters) {
        parameterHandler.addStoreStateParameters(aciParameters);
    }

    @Override
    public void addViewParameters(final AciParameters aciParameters, final String reference, final IdolViewRequest viewRequest) {
        parameterHandler.addViewParameters(aciParameters, reference, viewRequest);
    }
}
