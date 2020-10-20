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

package com.hp.autonomy.searchcomponents.core.search;

import java.util.Collection;

/**
 * Builder for {@link SearchRequest}
 *
 * @param <Q> The type of the query restrictions object
 * @param <B> Implementation of the builder
 */
@SuppressWarnings("unused")
public interface SearchRequestBuilder<R extends SearchRequest<Q>, Q extends QueryRestrictions<?>, B extends SearchRequestBuilder<R, Q, B>> {
    /**
     * Sets the query restrictions to apply
     *
     * @param queryRestrictions The query restrictions to apply
     * @return the builder (for chaining)
     */
    B queryRestrictions(Q queryRestrictions);

    /**
     * Sets the index of first result to display (1-based)
     *
     * @param start The index of first result to display (1-based)
     * @return the builder (for chaining)
     */
    B start(int start);

    /**
     * Sets the index of last result to display
     *
     * @param maxResults The index of last result to display
     * @return the builder (for chaining)
     */
    B maxResults(int maxResults);

    /**
     * Sets the maximum length of the summary
     *
     * @param summaryCharacters The maximum length of the summary
     * @return the builder (for chaining)
     */
    B summaryCharacters(Integer summaryCharacters);

    /**
     * Sets whether or not to apply search highlighting
     *
     * @param highlight Whether or not to apply search highlighting
     * @return the builder (for chaining)
     */
    B highlight(boolean highlight);

    /**
     * Sets the type of summary to generate
     *
     * @param summary The type of summary to generate
     * @return the builder (for chaining)
     */
    B summary(String summary);

    /**
     * Sets the criterion by which to order the results
     *
     * @param sort The criterion by which to order the results
     * @return the builder (for chaining)
     */
    B sort(String sort);

    /**
     * Sets what to display in the document result output
     *
     * @param print What to display in the document result output
     * @return the builder (for chaining)
     */
    B print(String print);

    /**
     * Sets the fields to display in the document result output if print is set to the 'PrintFields' option
     *
     * @param printFields The fields to display in the document result output if print is set to the 'PrintFields' option
     * @return the builder (for chaining)
     */
    B printFields(Collection<? extends String> printFields);

    /**
     * Sets a field to display in the document result output if print is set to the 'PrintFields' option
     *
     * @param printField A field to display in the document result output if print is set to the 'PrintFields' option
     * @return the builder (for chaining)
     */
    B printField(String printField);

    /**
     * Clears the collection of fields to display in the document result output
     *
     * @return the builder (for chaining)
     */
    B clearPrintFields();

    /**
     * Generates a new request object
     *
     * @return the new request object
     */
    R build();
}
