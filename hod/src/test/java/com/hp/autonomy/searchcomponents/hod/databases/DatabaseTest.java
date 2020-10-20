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

import com.hp.autonomy.searchcomponents.core.requests.ResponseObjectTest;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

public class DatabaseTest extends ResponseObjectTest<Database, Database.DatabaseBuilder> {
    @Override
    protected void validateJson(final JsonContent<Database> jsonContent) throws IOException {
        jsonContent.assertThat()
                .hasJsonPathStringValue("$.name", "wiki_eng")
                .hasJsonPathStringValue("$.displayName", "English Wikipedia")
                .hasJsonPathNumberValue("$.documents", 1234567890)
                .hasJsonPathBooleanValue("$.public", true)
                .hasJsonPathStringValue("$.domain", "PUBLIC_INDEXES");
    }

    @Override
    protected Database constructObject() {
        return Database.builder()
                .name("wiki_eng")
                .displayName("English Wikipedia")
                .documents(1234567890)
                .isPublic(true)
                .domain("PUBLIC_INDEXES")
                .build();
    }

    @Override
    protected String toStringContent() {
        return "domain";
    }
}
