/*
 * Copyright 2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.idol.view.configuration;

import com.autonomy.aci.client.services.AciService;
import com.hp.autonomy.frontend.configuration.server.ServerConfig;
import com.hp.autonomy.frontend.configuration.validation.ValidationResult;
import com.hp.autonomy.frontend.configuration.validation.Validator;
import com.opentext.idol.types.marshalling.ProcessorFactory;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

public class ViewConfigValidator implements Validator<ViewConfig> {
    private final AciService validatorAciService;
    private final ProcessorFactory processorFactory;

    public ViewConfigValidator(final AciService validatorAciService, final ProcessorFactory processorFactory) {
        this.validatorAciService = validatorAciService;
        this.processorFactory = processorFactory;
    }

    @Override
    public ValidationResult<?> validate(final ViewConfig config) {
        final ServerConfig serverConfig = ServerConfig.builder()
                .protocol(config.getProtocol())
                .host(config.getHost())
                .port(config.getPort())
                .serviceProtocol(config.getServiceProtocol())
                .servicePort(config.getServicePort())
                .productType(config.getProductType())
                .build();
        final ValidationResult<?> validationResult = serverConfig.validate(validatorAciService, null, processorFactory);

        ValidationResult<?> returnValue = null;
        if (validationResult.isValid()) {
            switch (config.getViewingMode()) {
                case CONNECTOR:
                    final ValidationResult<?> connectorValidation = config.getConnector().validate(validatorAciService, null, processorFactory);
                    returnValue = connectorValidation.isValid() ? validationResult : new ValidationResult<Object>(false, new ConnectorValidation(connectorValidation));
                    break;
                case FIELD:
                    returnValue = StringUtils.isBlank(config.getReferenceField()) ? new ValidationResult<>(false, Validation.REFERENCE_FIELD_BLANK) : validationResult;
                    break;
                case UNIVERSAL:
                    returnValue = new ValidationResult<>(true);
                    break;
            }
        } else {
            returnValue = validationResult;
        }

        return returnValue;
    }

    @Override
    public Class<ViewConfig> getSupportedClass() {
        return ViewConfig.class;
    }

    private enum Validation {
        REFERENCE_FIELD_BLANK,
        CONNECTOR_VALIDATION_ERROR
    }

    @Data
    private static class ConnectorValidation {
        private final Validation validation = Validation.CONNECTOR_VALIDATION_ERROR;
        private final ValidationResult<?> connectorValidation;
    }
}
