/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.core.search.AciSearchRequest;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndex;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;

/**
 * Common Aci parameter handling for related search queries
 */
public interface HavenSearchAciParameterHandler {
    void addSearchRestrictions(final AciParameters aciParameters, final QueryRestrictions<String> queryRestrictions);

    void addSearchOutputParameters(final AciParameters aciParameters, final AciSearchRequest<String> searchRequest);

    void addGetDocumentOutputParameters(final AciParameters aciParameters, final GetContentRequestIndex<String> indexAndReferences);

    void addLanguageRestriction(final AciParameters aciParameters, final QueryRestrictions<String> queryRestrictions);

    void addQmsParameters(final AciParameters aciParameters, final QueryRestrictions<String> queryRestrictions);
}
