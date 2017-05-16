/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collection;

/**
 * Builder for {@link QueryRestrictions}
 */
@SuppressWarnings("unused")
public interface QueryRestrictionsBuilder<Q extends QueryRestrictions<S>, S extends Serializable, B extends QueryRestrictionsBuilder<Q, S, B>>
        extends RequestObjectBuilder<QueryRestrictions<S>, QueryRestrictionsBuilder<?, S, ?>> {
    /**
     * Sets the query expression
     *
     * @param queryText The query expression
     * @return the builder (for chaining)
     */
    B queryText(String queryText);

    /**
     * Sets field text restrictions
     *
     * @param fieldText Field text restrictions
     * @return the builder (for chaining)
     */
    B fieldText(String fieldText);

    /**
     * Sets the databases to query
     *
     * @param databases The databases to query
     * @return the builder (for chaining)
     */
    B databases(Collection<? extends S> databases);

    /**
     * Sets a database to query
     *
     * @param database A database to query
     * @return the builder (for chaining)
     */
    B database(S database);

    /**
     * Clears the collection of databases to query
     *
     * @return the builder (for chaining)
     */
    B clearDatabases();

    /**
     * Sets the minimum date of results (uses configured date field)
     *
     * @param minDate The minimum date of results (uses configured date field)
     * @return the builder (for chaining)
     */
    B minDate(ZonedDateTime minDate);

    /**
     * Sets the maximum date of results (uses configured date field)
     *
     * @param maxDate The maximum date of results (uses configured date field)
     * @return the builder (for chaining)
     */
    B maxDate(ZonedDateTime maxDate);

    /**
     * Sets the minimum score threshold to apply
     *
     * @param minScore The minimum score threshold to apply
     * @return the builder (for chaining)
     */
    B minScore(Integer minScore);

    /**
     * Sets a language filter
     *
     * @param languageType A language filter
     * @return the builder (for chaining)
     */
    B languageType(String languageType);

    /**
     * Sets whether to return results irrespective of language
     *
     * @param anyLanguage Whether to return results irrespective of language
     * @return the builder (for chaining)
     */
    B anyLanguage(boolean anyLanguage);

    /**
     * Sets saved result set to include
     *
     * @param stateMatchId Saved result set to include
     * @return the builder (for chaining)
     */
    B stateMatchId(String stateMatchId);

    /**
     * Sets saved result sets to include
     *
     * @param stateMatchIds Saved result sets to include
     * @return the builder (for chaining)
     */
    B stateMatchIds(Collection<? extends String> stateMatchIds);

    /**
     * Clears collection of saved result sets to include
     *
     * @return the builder (for chaining)
     */
    B clearStateMatchIds();

    /**
     * Sets saved result set to exclude
     *
     * @param stateDontMatchId Saved result set to exclude
     * @return the builder (for chaining)
     */
    B stateDontMatchId(String stateDontMatchId);

    /**
     * Sets saved result sets to exclude
     *
     * @param stateDontMatchIds Saved result sets to exclude
     * @return the builder (for chaining)
     */
    B stateDontMatchIds(Collection<? extends String> stateDontMatchIds);

    /**
     * Clears collection of saved result sets to exclude
     *
     * @return the builder (for chaining)
     */
    B clearStateDontMatchIds();

    /**
     * {@inheritDoc}
     */
    @Override
    Q build();
}
