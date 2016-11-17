/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
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
