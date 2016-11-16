/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.test;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", defaultImpl = IdolQueryRestrictions.class)
@JsonSubTypes(@JsonSubTypes.Type(IdolQueryRestrictions.class))
public class IdolQueryRestrictionsMixin {
}
