/*
 * Copyright 2015-2017 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.requests.RequestObject;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Filter restrictions to apply to queries against Idol
 *
 * @param <S> The type of the database identifier
 */
public interface QueryRestrictions<S extends Serializable> extends RequestObject<QueryRestrictions<S>, QueryRestrictionsBuilder<?, S, ?>> {
    /**
     * The bean name of the default builder implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String QUERY_RESTRICTIONS_BUILDER_BEAN_NAME = "queryRestrictionsBuilder";

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
    ZonedDateTime getMinDate();

    /**
     * The maximum date of results (uses configured date field)
     *
     * @return The maximum date of results (uses configured date field)
     */
    ZonedDateTime getMaxDate();

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
     * Whether to return results irrespective of language
     *
     * @return Whether to return results irrespective of language
     */
    boolean isAnyLanguage();

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
}
