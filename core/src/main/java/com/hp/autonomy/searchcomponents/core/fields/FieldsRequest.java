/*
 * Copyright 2015 Open Text.
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

package com.hp.autonomy.searchcomponents.core.fields;

import com.hp.autonomy.searchcomponents.core.requests.RequestObject;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;

import java.util.Collection;

/**
 * Options for interacting with {@link FieldsService}
 */
@SuppressWarnings("unused")
public interface FieldsRequest extends RequestObject<FieldsRequest, FieldsRequestBuilder<?, ?>> {
    /**
     * The field types to retrieve
     *
     * @return The field types to retrieve
     */
    Collection<FieldTypeParam> getFieldTypes();

    /**
     * Max results to return in fields response
     *
     * @return Max results to return in fields response
     */
    Integer getMaxValues();
}
