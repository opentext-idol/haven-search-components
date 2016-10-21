/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.configuration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.SimpleComponent;
import com.hp.autonomy.frontend.configuration.server.ServerConfig;
import com.hp.autonomy.frontend.configuration.validation.OptionalConfigurationComponent;
import com.hp.autonomy.types.requests.qms.actions.typeahead.params.ModeParam;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.BooleanUtils;

@SuppressWarnings("DefaultAnnotationParam")
@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
@ToString
@JsonDeserialize(builder = QueryManipulation.QueryManipulationBuilder.class)
public class QueryManipulation extends SimpleComponent<QueryManipulation> implements OptionalConfigurationComponent<QueryManipulation> {
    private final ServerConfig server;
    private final Boolean expandQuery;
    private final String blacklist;
    private final ModeParam typeAheadMode;
    private final Boolean enabled;

    @Override
    public void basicValidate(final String section) throws ConfigException {
        if (BooleanUtils.isTrue(enabled)) {
            if (server == null) {
                throw new ConfigException("QMS", "QMS is enabled but no corresponding server details have been provided");
            }
            server.basicValidate("QMS");
        }
    }

    @SuppressWarnings("WeakerAccess")
    @JsonPOJOBuilder(withPrefix = "")
    public static class QueryManipulationBuilder {
    }
}
