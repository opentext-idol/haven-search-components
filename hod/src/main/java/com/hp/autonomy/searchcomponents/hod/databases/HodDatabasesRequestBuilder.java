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

package com.hp.autonomy.searchcomponents.hod.databases;

import com.hp.autonomy.searchcomponents.core.databases.DatabasesRequestBuilder;

/**
 * Builder for {@link HodDatabasesRequest}
 */
public interface HodDatabasesRequestBuilder extends DatabasesRequestBuilder<HodDatabasesRequest> {
    /**
     * Sets whether to return public indexes as well as private indexes
     *
     * @param publicIndexesEnabled Whether to return public indexes as well as private indexes
     * @return The builder (for chaining)
     */
    HodDatabasesRequestBuilder publicIndexesEnabled(boolean publicIndexesEnabled);
}
