/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.view.idol.configuration;

import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactory;
import com.autonomy.aci.client.services.AciService;
import com.hp.autonomy.frontend.configuration.ValidationResult;
import com.hp.autonomy.frontend.configuration.Validator;
import lombok.Setter;

@Setter
public class ViewConfigValidator implements Validator<ViewConfig> {

    private AciService testAciService;

    private IdolAnnotationsProcessorFactory idolAnnotationsProcessorFactory;

    @Override
    public ValidationResult<?> validate(final ViewConfig config) {
        return config.validate(testAciService, idolAnnotationsProcessorFactory);
    }

    @Override
    public Class<ViewConfig> getSupportedClass() {
        return ViewConfig.class;
    }
}
