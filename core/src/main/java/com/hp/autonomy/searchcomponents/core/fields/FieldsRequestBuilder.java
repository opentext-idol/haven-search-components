/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.fields;

import com.hp.autonomy.searchcomponents.core.requests.RequestObjectBuilder;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;

import java.util.Collection;

/**
 * Builder for {@link FieldsRequest}
 */
@SuppressWarnings("unused")
public interface FieldsRequestBuilder<R extends FieldsRequest, B extends FieldsRequestBuilder<R, B>>
        extends RequestObjectBuilder<FieldsRequest, FieldsRequestBuilder<?, ?>> {
    /**
     * Sets a field type to retrieve
     *
     * @param fieldTypeParam a field type to retrieve
     * @return the builder (for chaining)
     */
    B fieldType(FieldTypeParam fieldTypeParam);

    /**
     * Sets the field types to retrieve
     *
     * @param fieldTypes the field types to retrieve
     * @return the builder (for chaining)
     */
    B fieldTypes(Collection<? extends FieldTypeParam> fieldTypes);

    /**
     * Clears the field types to retrieve
     *
     * @return the builder (for chaining)
     */
    B clearFieldTypes();

    /**
     * Sets max results to return in fields response
     *
     * @param maxValues Max results to return in fields response
     * @return The builder (for chaining)
     */
    B maxValues(Integer maxValues);

    /**
     * {@inheritDoc}
     */
    @Override
    R build();
}
