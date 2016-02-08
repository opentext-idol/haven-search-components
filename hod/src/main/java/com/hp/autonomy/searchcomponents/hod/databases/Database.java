/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.databases;

import com.autonomy.aci.client.annotations.IdolBuilderBuild;
import com.hp.autonomy.types.IdolDatabase;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Database implements IdolDatabase, Comparable<Database> {
    private static final long serialVersionUID = -3966566623844850811L;
    static final String ROOT_FIELD = "database";

    private final String name;
    private final String displayName;
    private final long documents;
    private final boolean isPublic;
    private final String domain;

    @Override
    public int compareTo(final Database other) {
        return name.compareTo(other.name);
    }

    @Accessors(chain = true)
    @Setter
    public static class Builder {
        private String name;
        private String displayName;
        private long documents;
        private boolean isPublic;
        @SuppressWarnings("FieldMayBeFinal")
        private String domain = "";

        @IdolBuilderBuild
        public Database build() {
            return new Database(name, displayName, documents, isPublic, domain);
        }
    }
}
