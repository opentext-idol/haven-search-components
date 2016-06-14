/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.core.search.AciSearchRequest;
import com.hp.autonomy.types.idol.Hit;
import com.hp.autonomy.types.idol.QueryResponseData;
import com.hp.autonomy.types.requests.Documents;

import java.util.Collection;
import java.util.List;

/**
 * Parsing of response data returned by IDOL in response to search/suggest queries
 */
public interface QueryResponseParser {
    Documents<IdolSearchResult> parseQueryResults(AciSearchRequest<String> searchRequest, AciParameters aciParameters, QueryResponseData responseData, IdolDocumentService.QueryExecutor queryExecutor) throws SearchInvalidException;

    List<IdolSearchResult> parseQueryHits(Collection<Hit> hits);
}
