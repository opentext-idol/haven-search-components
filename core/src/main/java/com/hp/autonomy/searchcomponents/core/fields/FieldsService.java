/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.fields;

import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;
import java.util.Set;

/**
 * Service for retrieving lists of fields of given field types
 *
 * @param <R> The request type to use
 * @param <E> The checked exception thrown in the event of an error
 */
@FunctionalInterface
public interface FieldsService<R extends FieldsRequest, E extends Exception> {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String FIELD_SERVICE_BEAN_NAME = "fieldService";

    /**
     * The bean name of the default request builder implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String FIELDS_REQUEST_BUILDER_BEAN_NAME = "fieldsRequestBuilder";

    /**
     * Retrieves all fields of the given field types
     *
     * @param request options
     * @return list of fields per field type
     * @throws E The error thrown in the event of the platform returning an error response
     */
    Map<FieldTypeParam, Set<TagName>> getFields(final R request) throws E;
}