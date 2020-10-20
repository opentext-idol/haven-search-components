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

package com.hp.autonomy.searchcomponents.hod.fields;

import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequestBuilder;

import java.util.Collection;

/**
 * Builder for {@link HodFieldsRequest}
 */
@SuppressWarnings("unused")
public interface HodFieldsRequestBuilder extends FieldsRequestBuilder<HodFieldsRequest, HodFieldsRequestBuilder> {
    /**
     * Sets The indexes from which to retrieve fields
     *
     * @param databases The indexes from which to retrieve fields
     * @return The builder (for chaining)
     */
    HodFieldsRequestBuilder databases(Collection<? extends ResourceName> databases);

    /**
     * Sets an index from which to retrieve fields
     *
     * @param database An index from which to retrieve fields
     * @return The builder (for chaining)
     */
    HodFieldsRequestBuilder database(ResourceName database);

    /**
     * Clears the collection of indexes from which to retrieve fields
     *
     * @return The builder (for chaining)
     */
    HodFieldsRequestBuilder clearDatabases();
}
