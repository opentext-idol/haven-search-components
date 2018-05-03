/*
 * Copyright 2018 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.configuration;

import com.autonomy.aci.client.transport.AciServerDetails;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.SimpleComponent;
import com.hp.autonomy.frontend.configuration.server.ServerConfig;
import com.hp.autonomy.frontend.configuration.validation.OptionalConfigurationComponent;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Collection;

@SuppressWarnings("DefaultAnnotationParam")
@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
@ToString
@JsonDeserialize(builder = AnswerServerConfig.AnswerServerConfigBuilder.class)
public class AnswerServerConfig extends SimpleComponent<AnswerServerConfig> implements OptionalConfigurationComponent<AnswerServerConfig> {
    private static final String SECTION = "AnswerServer";

    private final ServerConfig server;
    @Singular
    private final Collection<String> systemNames;
    private final Boolean enabled;

    private final String conversationSystemName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Double conceptualMinScore;

    @Override
    public void basicValidate(final String configSection) throws ConfigException {
        if (BooleanUtils.isTrue(enabled)) {
            if (server == null) {
                throw new ConfigException(SECTION, "AnswerServer is enabled but no corresponding server details have been provided");
            }
            server.basicValidate(SECTION);
        }
    }

    public AciServerDetails toAciServerDetails() {
        return server.toAciServerDetails();
    }

    @SuppressWarnings("WeakerAccess")
    @JsonPOJOBuilder(withPrefix = "")
    public static class AnswerServerConfigBuilder {
    }
}
