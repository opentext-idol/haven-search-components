/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.databases;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public final class Database implements Serializable {
    private static final long serialVersionUID = -3966566623844850811L;

    private final String name;
    private final long documents;
    private final boolean isPublic;
    private final String domain;
    private final Set<String> fieldNames;

    private Database(final String name, final long documents, final boolean isPublic, final String domain, final Set<String> fieldNames) {
        this.documents = documents;
        this.name = name;
        this.isPublic = isPublic;
        this.domain = domain;
        this.fieldNames = fieldNames;
    }

    public static class Builder {
        private String name;
        private long documents;
        private boolean isPublic;
        private String domain = "";
        private Set<String> indexFields;

        public Builder setName(final String name) {
            this.name = name.toLowerCase();
            return this;
        }

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
            this.indexFields = indexFields;
            return this;
        }

        public Database build() {
            return new Database(name, documents, isPublic, domain, indexFields);
        }
    }
}
