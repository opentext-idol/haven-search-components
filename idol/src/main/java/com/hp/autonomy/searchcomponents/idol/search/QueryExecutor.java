/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.core.search.QueryRequest;
import com.hp.autonomy.types.idol.responses.GetQueryTagValuesResponseData;
import com.hp.autonomy.types.idol.responses.QueryResponseData;
import com.hp.autonomy.types.idol.responses.SuggestResponseData;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Implementations are responsible for executing queries against IDOL for a given set of parameters and query type
 */
@SuppressWarnings("WeakerAccess")
public interface QueryExecutor {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String QUERY_EXECUTOR_BEAN_NAME = "queryExecutor";

    /**
     * Whether or not it is worth performing a query
     * Querying for promotions is pointless and misleading if QMS is not configured
     *
     * @param queryType the type of query being performed
     * @return Whether or not to perform a query
     * @throws AciErrorException The error thrown in the event of the IDOL's returning an error response
     */
    boolean performQuery(QueryRequest.QueryType queryType) throws AciErrorException;

    /**
     * Executes a query action
     *
     * @param aciParameters the query parameters to send to IDOL
     * @param queryType     the type of query being performed
     * @return The IDOL response data
     * @throws AciErrorException The error thrown in the event of the IDOL's returning an error response
     */
    QueryResponseData executeQuery(AciParameters aciParameters, QueryRequest.QueryType queryType) throws AciErrorException;

    /**
     * Executes a suggest action
     *
     * @param aciParameters the query parameters to send to IDOL
     * @param queryType     the type of query being performed
     * @return The IDOL response data
     * @throws AciErrorException The error thrown in the event of the IDOL's returning an error response
     */
    SuggestResponseData executeSuggest(AciParameters aciParameters, QueryRequest.QueryType queryType) throws AciErrorException;

    /**
     *
     * @param aciParameters the query parameters to send to IDOL
     * @param queryType the type of query being performed
     * @return The IDOL response data
     * @throws AciErrorException The error thrown in the event of the IDOL's returning an error response
     */
    GetQueryTagValuesResponseData executeGetQueryTagValues(AciParameters aciParameters, QueryRequest.QueryType queryType) throws AciErrorException;
}
