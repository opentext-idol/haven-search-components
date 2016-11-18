/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.databases;

import com.hp.autonomy.searchcomponents.core.requests.RequestObject;
import com.hp.autonomy.types.IdolDatabase;
import lombok.Builder;
import lombok.Data;

@SuppressWarnings("WeakerAccess")
@Data
@Builder(toBuilder = true)
public final class Database implements IdolDatabase, Comparable<Database>, RequestObject<Database, Database.DatabaseBuilder> {
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

    public static class DatabaseBuilder implements RequestObject.RequestObjectBuilder<Database, DatabaseBuilder> {}
}
