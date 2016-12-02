/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import java.util.Collection;

/**
 * Common request functionality between Search and Suggest queries
 *
 * @param <Q> The type of the query restrictions object
 */
public interface SearchRequest<Q extends QueryRestrictions<?>> {
    int DEFAULT_START = 1;
    int DEFAULT_MAX_RESULTS = 30;

    /**
     * The query restrictions to apply
     *
     * @return The query restrictions to apply
     */
    Q getQueryRestrictions();

    /**
     * Index of first result to display (1-based)
     *
     * @return Index of first result to display (1-based)
     */
    int getStart();

    /**
     * Index of last result to display
     *
     * @return Index of last result to display
     */
    int getMaxResults();

    /**
     * The maximum length of the summary
     *
     * @return The maximum length of the summary
     */
    Integer getSummaryCharacters();

    /**
     * Whether or not to apply search highlighting
     *
     * @return Whether or not to apply search highlighting
     */
    boolean isHighlight();

    /**
     * The type of summary to generate
     *
     * @return The type of summary to generate
     */
    String getSummary();

    /**
     * The criterion by which to order the results
     *
     * @return The criterion by which to order the results
     */
    String getSort();

    /**
     * What to display in the document result output
     *
     * @return What to display in the document result output
     */
    String getPrint();

    /**
     * The fields to display in the document result output if print is set to the 'PrintFields' option
     *
     * @return The fields to display in the document result output if print is set to the 'PrintFields' option
     */
    Collection<String> getPrintFields();
}
