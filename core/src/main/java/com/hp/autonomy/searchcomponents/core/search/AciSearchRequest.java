/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;

import java.io.Serializable;
import java.util.Collection;

/**
 * Common request functionality between Search and Suggest queries
 *
 * @param <S> The type of the database identifier
 */
public interface AciSearchRequest<S extends Serializable> {
    int DEFAULT_START = 1;
    int DEFAULT_MAX_RESULTS = 30;
    String DEFAULT_PRINT = PrintParam.Fields.name();

    /**
     * The query restrictions to apply
     *
     * @return The query restrictions to apply
     */
    QueryRestrictions<S> getQueryRestrictions();

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
     * The type of summary to generate
     *
     * @return The type of summary to generate
     */
    String getSummary();

    /**
     * The maximum length of the summary
     *
     * @return The maximum length of the summary
     */
    Integer getSummaryCharacters();

    /**
     * The criterion by which to order the results
     *
     * @return The criterion by which to order the results
     */
    String getSort();

    /**
     * Whether or not to apply search highlighting
     *
     * @return Whether or not to apply search highlighting
     */
    boolean isHighlight();

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
