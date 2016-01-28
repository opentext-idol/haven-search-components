/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.view.configuration;

import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactory;
import com.autonomy.aci.client.services.AciService;
import com.hp.autonomy.frontend.configuration.ValidationResult;
import com.hp.autonomy.frontend.configuration.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ViewConfigValidator implements Validator<ViewConfig> {

    private final AciService validatorAciService;
    private final IdolAnnotationsProcessorFactory idolAnnotationsProcessorFactory;

    @Autowired
    public ViewConfigValidator(final AciService validatorAciService, final IdolAnnotationsProcessorFactory idolAnnotationsProcessorFactory) {
        this.validatorAciService = validatorAciService;
        this.idolAnnotationsProcessorFactory = idolAnnotationsProcessorFactory;
    }

    @Override
    public ValidationResult<?> validate(final ViewConfig config) {
        return config.validate(validatorAciService, idolAnnotationsProcessorFactory);
    }

    @Override
    public Class<ViewConfig> getSupportedClass() {
        return ViewConfig.class;
    }
}
