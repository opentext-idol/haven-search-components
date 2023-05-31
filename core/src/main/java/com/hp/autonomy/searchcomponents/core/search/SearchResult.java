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
import com.hp.autonomy.searchcomponents.core.requests.RequestObjectBuilder;

import java.time.ZonedDateTime;

/**
 * Response object for Query actions in {@link DocumentsService}
 */
@SuppressWarnings("unused")
public interface SearchResult extends RequestObject<SearchResult, SearchResult.SearchResultBuilder> {
    /**
     * The document reference
     *
     * @return The document reference
     */
    String getReference();

    /**
     * The Database/Index in which the document is located
     *
     * @return The Database/Index in which the document is located
     */
    String getIndex();

    /**
     * The document title
     *
     * @return The document title
     */
    String getTitle();

    /**
     * The document summary
     *
     * @return The document summary
     */
    String getSummary();

    /**
     * The weight assigned to the document against the current query
     *
     * @return The weight assigned to the document against the current query
     */
    Double getWeight();

    /**
     * The document date (from configured date field)
     *
     * @return The document date (from configured date field)
     */
    ZonedDateTime getDate();

    /**
     * The type of promotion the document represents
     *
     * @return The type of promotion the document represents
     */
    PromotionCategory getPromotionCategory();

    /**
     * Builder
     */
    interface SearchResultBuilder extends RequestObjectBuilder<SearchResult, SearchResultBuilder> {
        /**
         * Sets the document reference
         *
         * @param reference The document reference
         * @return the builder (for chaining)
         */
        SearchResultBuilder reference(final String reference);

        /**
         * Sets the Database/Index in which the document is located
         *
         * @param index The Database/Index in which the document is located
         * @return the builder (for chaining)
         */
        SearchResultBuilder index(final String index);

        /**
         * Sets the document title
         *
         * @param title The document title
         * @return the builder (for chaining)
         */
        SearchResultBuilder title(final String title);

        /**
         * Sets the document summary
         *
         * @param summary The document summary
         * @return the builder (for chaining)
         */
        SearchResultBuilder summary(final String summary);

        /**
         * Sets the weight assigned to the document against the current query
         *
         * @param weight The weight assigned to the document against the current query
         * @return the builder (for chaining)
         */
        SearchResultBuilder weight(final Double weight);

        /**
         * Sets the document date
         *
         * @param date The document date
         * @return the builder (for chaining)
         */
        SearchResultBuilder date(final ZonedDateTime date);

        /**
         * Sets the type of promotion the document represents
         *
         * @param promotionCategory The type of promotion the document represents
         * @return the builder (for chaining)
         */
        SearchResultBuilder promotionCategory(final PromotionCategory promotionCategory);
    }
}
