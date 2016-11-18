/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.requests.RequestObject;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Filter restrictions to apply to queries against Idol
 *
 * @param <S> The type of the database identifier
 */
public interface QueryRestrictions<S extends Serializable>
        extends RequestObject<QueryRestrictions<S>, QueryRestrictions.QueryRestrictionsBuilder<?, S>> {
    /**
     * Query expression
     *
     * @return Query expression
     */
    String getQueryText();

    /**
     * Field text restriction
     *
     * @return Field text restriction
     */
    String getFieldText();

    /**
     * The databases to query
     *
     * @return The databases to query
     */
    List<S> getDatabases();

    /**
     * The minimum date of results (uses configured date field)
     *
     * @return The minimum date of results (uses configured date field)
     */
    DateTime getMinDate();

    /**
     * The maximum date of results (uses configured date field)
     *
     * @return The maximum date of results (uses configured date field)
     */
    DateTime getMaxDate();

    /**
     * The minimum score threshold to apply
     *
     * @return The minimum score threshold to apply
     */
    Integer getMinScore();

    /**
     * A language filter
     *
     * @return a language filter
     */
    String getLanguageType();

    /**
     * Saved result sets to include
     *
     * @return Saved result sets to include
     */
    List<String> getStateMatchIds();

    /**
     * Saved result sets to exclude
     *
     * @return Saved result sets to exclude
     */
    List<String> getStateDontMatchIds();

    /**
     * Whether to return results irrespective of language
     *
     * @return Whether to return results irrespective of language
     */
    boolean isAnyLanguage();

    @SuppressWarnings("unused")
    interface QueryRestrictionsBuilder<Q extends QueryRestrictions<S>, S extends Serializable>
            extends RequestObject.RequestObjectBuilder<QueryRestrictions<S>, QueryRestrictionsBuilder<?, S>> {
        /**
         * Sets the query expression
         *
         * @param queryText The query expression
         * @return the builder (for chaining)
         */
        QueryRestrictionsBuilder<Q, S> queryText(String queryText);

        /**
         * Sets field text restrictions
         *
         * @param fieldText Field text restrictions
         * @return the builder (for chaining)
         */
        QueryRestrictionsBuilder<Q, S> fieldText(String fieldText);

        /**
         * Sets the databases to query
         *
         * @param databases The databases to query
         * @return the builder (for chaining)
         */
        QueryRestrictionsBuilder<Q, S> databases(Collection<? extends S> databases);

        /**
         * Sets a database to query
         *
         * @param database A database to query
         * @return the builder (for chaining)
         */
        QueryRestrictionsBuilder<Q, S> database(S database);

        /**
         * Clears the collection of databases to query
         *
         * @return the builder (for chaining)
         */
        QueryRestrictionsBuilder<Q, S> clearDatabases();

        /**
         * Sets the minimum date of results (uses configured date field)
         *
         * @param minDate The minimum date of results (uses configured date field)
         * @return the builder (for chaining)
         */
        QueryRestrictionsBuilder<Q, S> minDate(DateTime minDate);

        /**
         * Sets the maximum date of results (uses configured date field)
         *
         * @param maxDate The maximum date of results (uses configured date field)
         * @return the builder (for chaining)
         */
        QueryRestrictionsBuilder<Q, S> maxDate(DateTime maxDate);

        /**
         * Sets the minimum score threshold to apply
         *
         * @param minScore The minimum score threshold to apply
         * @return the builder (for chaining)
         */
        QueryRestrictionsBuilder<Q, S> minScore(Integer minScore);

        /**
         * Sets a language filter
         *
         * @param languageType A language filter
         * @return the builder (for chaining)
         */
        QueryRestrictionsBuilder<Q, S> languageType(String languageType);

        /**
         * Sets saved result set to include
         *
         * @param stateMatchId Saved result set to include
         * @return the builder (for chaining)
         */
        QueryRestrictionsBuilder<Q, S> stateMatchId(String stateMatchId);

        /**
         * Sets saved result sets to include
         *
         * @param stateMatchIds Saved result sets to include
         * @return the builder (for chaining)
         */
        QueryRestrictionsBuilder<Q, S> stateMatchIds(Collection<? extends String> stateMatchIds);

        /**
         * Clears collection of saved result sets to include
         *
         * @return the builder (for chaining)
         */
        QueryRestrictionsBuilder<Q, S> clearStateMatchIds();

        /**
         * Sets saved result set to exclude
         *
         * @param stateDontMatchId Saved result set to exclude
         * @return the builder (for chaining)
         */
        QueryRestrictionsBuilder<Q, S> stateDontMatchId(String stateDontMatchId);

        /**
         * Sets saved result sets to exclude
         *
         * @param stateDontMatchIds Saved result sets to exclude
         * @return the builder (for chaining)
         */
        QueryRestrictionsBuilder<Q, S> stateDontMatchIds(Collection<? extends String> stateDontMatchIds);

        /**
         * Clears collection of saved result sets to exclude
         *
         * @return the builder (for chaining)
         */
        QueryRestrictionsBuilder<Q, S> clearStateDontMatchIds();

        /**
         * Sets whether to return results irrespective of language
         *
         * @param anyLanguage Whether to return results irrespective of language
         * @return the builder (for chaining)
         */
        QueryRestrictionsBuilder<Q, S> anyLanguage(boolean anyLanguage);

        /**
         * {@inheritDoc}
         */
        @Override
        Q build();
    }
}
