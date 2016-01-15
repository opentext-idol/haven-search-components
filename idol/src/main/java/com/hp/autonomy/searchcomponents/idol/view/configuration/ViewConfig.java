/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.view.configuration;

import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactory;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.transport.AciServerDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.ConfigurationComponent;
import com.hp.autonomy.frontend.configuration.ProductType;
import com.hp.autonomy.frontend.configuration.ServerConfig;
import com.hp.autonomy.frontend.configuration.ValidationResult;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

@JsonDeserialize(builder = ViewConfig.Builder.class)
public class ViewConfig implements ConfigurationComponent {

    private final ServerConfig serverConfig;

    @Getter
    private final String referenceField;

    private ViewConfig(final ServerConfig serverConfig, final String referenceField) {
        this.serverConfig = serverConfig;
        this.referenceField = referenceField;
    }

    public ViewConfig merge(final ViewConfig other) {
        if(other != null) {
            final ServerConfig serverConfig = this.serverConfig.merge(other.serverConfig);

            return new Builder()
                    .setProtocol(serverConfig.getProtocol())
                    .setServiceProtocol(serverConfig.getServiceProtocol())
                    .setHost(serverConfig.getHost())
                    .setPort(serverConfig.getPort())
                    .setServicePort(serverConfig.getServicePort())
                    .setProductType(serverConfig.getProductType())
                    .setReferenceField(referenceField == null ? other.referenceField : this.referenceField)
                    .build();
        }

        return this;
    }

    public boolean basicValidate(final String component) throws ConfigException {
        return serverConfig.basicValidate(component);
    }

    public ValidationResult<?> validate(final AciService aciService, final IdolAnnotationsProcessorFactory idolAnnotationsProcessorFactory) {
        final ValidationResult<?> validationResult = serverConfig.validate(aciService, null, idolAnnotationsProcessorFactory);

        if(validationResult.isValid()) {
            if(StringUtils.isBlank(referenceField)) {
                return new ValidationResult<>(false, Validation.REFERENCE_FIELD_BLANK);
            } else {
                return validationResult;
            }
        } else {
            return validationResult;
        }
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    public AciServerDetails.TransportProtocol getProtocol() {
        return serverConfig.getProtocol();
    }

    public AciServerDetails.TransportProtocol getServiceProtocol() {
        return serverConfig.getServiceProtocol();
    }

    public String getHost() {
        return serverConfig.getHost();
    }

    public int getPort() {
        return serverConfig.getPort();
    }

    public int getServicePort() {
        return serverConfig.getServicePort();
    }

    public Set<ProductType> getProductType() {
        return serverConfig.getProductType();
    }

    public AciServerDetails toAciServerDetails() {
        return serverConfig.toAciServerDetails();
    }

    @JsonPOJOBuilder(withPrefix = "set")
    @NoArgsConstructor
    public static class Builder {
        private String referenceField;

        private final ServerConfig.Builder builder = new ServerConfig.Builder();

        public ViewConfig build() {
            return new ViewConfig(builder.build(), referenceField);
        }

        public Builder setProtocol(final AciServerDetails.TransportProtocol protocol) {
            builder.setProtocol(protocol);
            return this;
        }

        public Builder setServiceProtocol(final AciServerDetails.TransportProtocol serviceProtocol) {
            builder.setServiceProtocol(serviceProtocol);
            return this;
        }

        public Builder setHost(final String host) {
            builder.setHost(host);
            return this;
        }

        public Builder setPort(final int port) {
            builder.setPort(port);
            return this;
        }

        public Builder setServicePort(final int servicePort) {
            builder.setServicePort(servicePort);
            return this;
        }

        public Builder setProductType(final Set<ProductType> productTypes) {
            builder.setProductType(productTypes);
            return this;
        }

        public Builder setReferenceField(final String referenceField) {
            this.referenceField = referenceField;
            return this;
        }
    }

    private enum Validation {
        REFERENCE_FIELD_BLANK
    }
}
