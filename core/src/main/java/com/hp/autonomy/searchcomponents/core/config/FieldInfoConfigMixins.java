/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Set;

/**
 * Json prettification when generating config output.
 * All default values are not printed out.
 *
 * @param <T> The field value type
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public interface FieldInfoConfigMixins<T> {
    @JsonIgnore
    String getId();

    @JsonProperty("type")
    String getTypeAsString();

    @JsonProperty("advanced")
    Boolean isAdvancedIfNotDefault();

    @JsonProperty("names")
    Set<String> getNamesIfNotEmpty();

    @JsonIgnore
    List<T> getValues();
}
