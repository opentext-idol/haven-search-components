/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.requests;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@SuppressWarnings({"unused", "WeakerAccess"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", defaultImpl = IdolGetContentRequestIndexImpl.class)
@JsonSubTypes(@JsonSubTypes.Type(IdolGetContentRequestIndexImpl.class))
public interface IdolGetContentRequestIndexMixin {
}
