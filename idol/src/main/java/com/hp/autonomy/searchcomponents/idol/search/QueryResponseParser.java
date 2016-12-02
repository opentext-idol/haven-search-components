/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.types.idol.responses.Hit;
import com.hp.autonomy.types.idol.responses.QueryResponseData;
import com.hp.autonomy.types.requests.Documents;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Parsing of response data returned by IDOL in response to search/suggest queries
 */
public interface QueryResponseParser {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String QUERY_RESPONSE_PARSER_BEAN_NAME = "queryResponseParser";

    /**
     * Parse Idol query response
     *
     * @param searchRequest The query request options
     * @param aciParameters The parameters sent in the Idol request
     * @param responseData  The Idol response
     * @param queryExecutor The function used for executing the query (in case it needs to be rerun)
     * @return The parsed query results
     */
    Documents<IdolSearchResult> parseQueryResults(IdolSearchRequest searchRequest, AciParameters aciParameters, QueryResponseData responseData, Function<AciParameters, QueryResponseData> queryExecutor);

    /**
     * Parses the "hits" section of the Idol query response
     *
     * @param hits The "hits" section of the Idol query response
     * @return The parsed query results
     */
    List<IdolSearchResult> parseQueryHits(Collection<Hit> hits);
}
