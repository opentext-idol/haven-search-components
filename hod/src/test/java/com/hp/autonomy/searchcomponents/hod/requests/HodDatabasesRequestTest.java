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

package com.hp.autonomy.searchcomponents.hod.requests;

import com.hp.autonomy.searchcomponents.core.databases.DatabasesRequest;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesRequestTest;

public class HodDatabasesRequestTest extends DatabasesRequestTest {
    @Override
    protected DatabasesRequest constructObject() {
        return HodDatabasesRequestImpl.builder().publicIndexesEnabled(true).build();
    }

    @Override
    protected String json() {
        return "{\"publicIndexesEnabled\": true}";
    }

    @Override
    protected String toStringContent() {
        return "publicIndexesEnabled";
    }
}
