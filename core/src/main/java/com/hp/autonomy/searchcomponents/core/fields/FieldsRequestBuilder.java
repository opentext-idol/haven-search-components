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
