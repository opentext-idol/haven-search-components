/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;

/**
 * Common Aci parameter handling for related search queries
 */
public interface HavenSearchAciParameterHandler {
    void addSearchRestrictions(final AciParameters aciParameters, final QueryRestrictions<String> queryRestrictions);
    void addSearchOutputParameters(final AciParameters aciParameters, final SearchRequest<String> searchRequest);
    void addLanguageRestriction(final AciParameters aciParameters, final QueryRestrictions<String> queryRestrictions);
    void addQmsParameters(final AciParameters aciParameters, final QueryRestrictions<String> queryRestrictions);
}
