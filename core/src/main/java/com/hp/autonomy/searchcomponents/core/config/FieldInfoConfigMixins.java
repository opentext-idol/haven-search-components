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

package com.hp.autonomy.searchcomponents.core.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;

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
    Set<FieldPath> getNamesIfNotEmpty();
}
