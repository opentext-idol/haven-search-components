/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.core.search.AciSearchRequest;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndex;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Common Aci parameter handling for related search queries
 */
public interface HavenSearchAciParameterHandler {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String PARAMETER_HANDLER_BEAN_NAME = "parameterHandler";

    /**
     * Adds Idol parameters for restricting/filtering search results
     *
     * @param aciParameters The set of parameters to add to
     * @param queryRestrictions The restrictions to add
     */
    void addSearchRestrictions(final AciParameters aciParameters, final QueryRestrictions<String> queryRestrictions);

    /**
     * Adds Idol parameters relating to the format/content of the response objects returned by Idol
     *
     * @param aciParameters The set of parameters to add to
     * @param searchRequest The request options
     */
    void addSearchOutputParameters(final AciParameters aciParameters, final AciSearchRequest<String> searchRequest);

    /**
     * Adds Idol parameters relating to requests for retrieving document content
     *
     * @param aciParameters The set of parameters to add to
     * @param indexAndReferences The databases and references of documents to be retrieved
     * @param print the print setting, determining Idol output
     */
    void addGetDocumentOutputParameters(final AciParameters aciParameters, final GetContentRequestIndex<String> indexAndReferences, final PrintParam print);

    /**
     * Adds Idol parameters for retrieving document content (during View document process)
     *
     * @param aciParameters The set of parameters to add to
     * @param database Database in which the document is located
     * @param documentReference Reference of the document
     * @param referenceField Document field which represents the reference
     */
    void addGetContentOutputParameters(final AciParameters aciParameters, final String database, final String documentReference, final String referenceField);

    /**
     * Adds parameters for restrictions (or absence of restrictions) relating to language
     *
     * @param aciParameters The set of parameters to add to
     * @param queryRestrictions The restrictions to add (includes language restrictions)
     */
    void addLanguageRestriction(final AciParameters aciParameters, final QueryRestrictions<String> queryRestrictions);

    /**
     * Adds parameters which only apply when query QMS (and should not be added when querying Content)
     *
     * @param aciParameters The set of parameters to add to
     * @param queryRestrictions The restrictions to add
     */
    void addQmsParameters(final AciParameters aciParameters, final QueryRestrictions<String> queryRestrictions);

    /**
     * Adds security info String which may allow restricted documents to be accessed
     *
     * @param aciParameters The set of parameters to add to
     */
    void addSecurityInfo(AciParameters aciParameters);

    /**
     * Adds parameters used when retrieving state tokens for a query
     *
     * @param aciParameters The set of parameters to add to
     */
    void addStoreStateParameters(AciParameters aciParameters);
}
