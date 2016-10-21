/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.configuration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.SimpleComponent;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings("DefaultAnnotationParam")
@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
@ToString
@JsonDeserialize(builder = QueryManipulationConfig.QueryManipulationConfigBuilder.class)
public class QueryManipulationConfig extends SimpleComponent<QueryManipulationConfig> {
    private static final String SECTION = "queryManipulation";

    private final String profile;
    private final String index;

    @Override
    public void basicValidate(final String section) throws ConfigException {
        if (StringUtils.isBlank(profile)) {
            throw new ConfigException(SECTION, "Query profile is required");
        }

        if (StringUtils.isBlank(index)) {
            throw new ConfigException(SECTION, "Query manipulation index is required");
        }
    }

    @SuppressWarnings("WeakerAccess")
    @JsonPOJOBuilder(withPrefix = "")
    public static class QueryManipulationConfigBuilder {
    }
}
