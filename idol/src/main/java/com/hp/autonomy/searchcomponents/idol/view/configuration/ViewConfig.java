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

package com.hp.autonomy.searchcomponents.idol.view.configuration;

import com.autonomy.aci.client.transport.AciServerDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.frontend.configuration.SimpleComponent;
import com.hp.autonomy.frontend.configuration.server.ProductType;
import com.hp.autonomy.frontend.configuration.server.ServerConfig;
import com.hp.autonomy.frontend.configuration.validation.OptionalConfigurationComponent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@SuppressWarnings({"DefaultAnnotationParam", "WeakerAccess"})
@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@JsonDeserialize(builder = ViewConfig.ViewConfigBuilder.class)
public class ViewConfig extends SimpleComponent<ViewConfig> implements OptionalConfigurationComponent<ViewConfig> {

    @Getter(AccessLevel.NONE)
    private final ServerConfig serverConfig;

    private final ServerConfig connector;
    private final String referenceField;
    private final ViewingMode viewingMode;
    private final Boolean highlighting;

    @Override
    @JsonIgnore
    public Boolean getEnabled() {
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

    public Integer getPort() {
        return serverConfig.getPort();
    }

    public Integer getServicePort() {
        return serverConfig.getServicePort();
    }

    public Set<ProductType> getProductType() {
        return serverConfig.getProductType();
    }

    public AciServerDetails toAciServerDetails() {
        return serverConfig.toAciServerDetails();
    }

    @SuppressWarnings({"WeakerAccess", "FieldMayBeFinal", "unused"})
    @JsonPOJOBuilder(withPrefix = "")
    public static class ViewConfigBuilder {
        private final ServerConfig.ServerConfigBuilder builder = ServerConfig.builder();
        private ServerConfig serverConfig;
        private ServerConfig connector;
        private String referenceField;
        private ViewingMode viewingMode = ViewingMode.FIELD;
        private Boolean highlighting;

        public ViewConfig build() {
            return new ViewConfig(serverConfig != null ? serverConfig : builder.build(), connector, referenceField, viewingMode, highlighting);
        }

        public ViewConfigBuilder protocol(final AciServerDetails.TransportProtocol protocol) {
            builder.protocol(protocol);
            return this;
        }

        public ViewConfigBuilder serviceProtocol(final AciServerDetails.TransportProtocol serviceProtocol) {
            builder.serviceProtocol(serviceProtocol);
            return this;
        }

        public ViewConfigBuilder host(final String host) {
            builder.host(host);
            return this;
        }

        public ViewConfigBuilder port(final Integer port) {
            builder.port(port);
            return this;
        }

        public ViewConfigBuilder servicePort(final Integer servicePort) {
            builder.servicePort(servicePort);
            return this;
        }

        public ViewConfigBuilder productType(final Set<ProductType> productTypes) {
            builder.productType(productTypes);
            return this;
        }
    }
}
