/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.idol.view.IdolViewRequest;
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
     * @param aciParameters     The set of parameters to add to
     * @param queryRestrictions The restrictions to add
     */
    void addSearchRestrictions(final AciParameters aciParameters, final IdolQueryRestrictions queryRestrictions);

    /**
     * Adds Idol parameters relating to the format/content of the response objects returned by Idol
     *
     * @param aciParameters The set of parameters to add to
     * @param searchRequest The request options
     */
    void addSearchOutputParameters(final AciParameters aciParameters, final IdolSearchRequest searchRequest);

    /**
     * Adds Idol parameters relating to requests for retrieving document content
     *
     * @param aciParameters      The set of parameters to add to
     * @param indexAndReferences The databases and references of documents to be retrieved
     * @param request            The request options
     */
    void addGetDocumentOutputParameters(final AciParameters aciParameters, final IdolGetContentRequestIndex indexAndReferences, final IdolGetContentRequest request);

    /**
     * Adds Idol parameters for retrieving document content (during View document process)
     *
     * @param aciParameters     The set of parameters to add to
     * @param database          Database in which the document is located
     * @param documentReference Reference of the document
     * @param referenceField    Document field which represents the reference (a field name in content, not a view URL).
     */
    void addGetContentOutputParameters(final AciParameters aciParameters, final String database, final String documentReference, final String referenceField);

    /**
     * Adds parameters for restrictions (or absence of restrictions) relating to language
     *
     * @param aciParameters     The set of parameters to add to
     * @param queryRestrictions The restrictions to add (includes language restrictions)
     */
    void addLanguageRestriction(final AciParameters aciParameters, final IdolQueryRestrictions queryRestrictions);

    /**
     * Adds parameters which only apply when query QMS (and should not be added when querying Content)
     *
     * @param aciParameters     The set of parameters to add to
     * @param queryRestrictions The restrictions to add
     */
    void addQmsParameters(final AciParameters aciParameters, final IdolQueryRestrictions queryRestrictions);

    void addIntentBasedRankingParameters(AciParameters aciParameters);

    /**
     * @return Security info for the current user
     */
    String getSecurityInfo();

    /**
     * Adds security info String which may allow restricted documents to be accessed
     *
     * @param aciParameters The set of parameters to add to
     */
    void addSecurityInfo(AciParameters aciParameters);

    /**
     * Adds parameters to the request which can be used to identify the user
     * @param aciParameters
     */
    void addUserIdentifiers(AciParameters aciParameters);

    /**
     * Adds parameters used when retrieving state tokens for a query
     *
     * @param aciParameters The set of parameters to add to
     */
    void addStoreStateParameters(AciParameters aciParameters);

    /**
     * Adds parameters for performing a view action against ViewServer
     *
     * @param aciParameters The set of parameters to add to
     * @param reference     The document reference to view
     * @param viewRequest   View request options
     */
    void addViewParameters(AciParameters aciParameters, String reference, IdolViewRequest viewRequest);
}
