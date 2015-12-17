/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.hod.databases;

import com.autonomy.aci.client.annotations.IdolBuilder;
import com.autonomy.aci.client.annotations.IdolBuilderBuild;
import com.autonomy.aci.client.annotations.IdolDocument;
import com.autonomy.aci.client.annotations.IdolField;
import com.hp.autonomy.types.IdolDatabase;
import lombok.Data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Data
@IdolDocument(Database.ROOT_FIELD)
public final class Database implements IdolDatabase {
    private static final long serialVersionUID = -3966566623844850811L;
    static final String ROOT_FIELD = "database";

    private final String name;
    private final String displayName;
    private final long documents;
    private final boolean isPublic;
    private final String domain;
    private final Set<String> fieldNames;

    private Database(final String name, final String displayName, final long documents, final boolean isPublic, final String domain, final Set<String> fieldNames) {
        this.documents = documents;
        this.name = name;
        this.displayName = displayName;
        this.isPublic = isPublic;
        this.domain = domain;
        this.fieldNames = fieldNames == null ? Collections.<String>emptySet() : fieldNames;
    }

    @IdolBuilder
    @IdolDocument(ROOT_FIELD)
    public static class Builder {
        private String name;
        private String displayName;
        private long documents;
        private boolean isPublic;
        private String domain = "";
        private Set<String> indexFields;

        @IdolField("name")
        public Builder setName(final String name) {
            this.name = name.toLowerCase();
            return this;
        }

        public Builder setDisplayName(final String displayName) {
            this.displayName = displayName;
            return this;
        }

        @IdolField("documents")
        public Builder setDocuments(final long documents) {
            this.documents = documents;
            return this;
        }

        public Builder setIsPublic(final boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public Builder setDomain(final String domain) {
            this.domain = domain;
            return this;
        }

        public Builder setIndexFields(final Set<String> indexFields) {
            this.indexFields = new HashSet<>(indexFields);
            return this;
        }

        @IdolBuilderBuild
        public Database build() {
            return new Database(name, displayName, documents, isPublic, domain, indexFields);
        }
    }
}
