/*
 * Copyright 2015-2016 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.configuration;

import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactory;
import com.autonomy.aci.client.services.AciService;
import com.hp.autonomy.frontend.configuration.ValidationResult;
import com.hp.autonomy.frontend.configuration.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueryManipulationValidator implements Validator<QueryManipulation> {

    private final AciService aciService;
    private final IdolAnnotationsProcessorFactory processorFactory;

    @Autowired
    public QueryManipulationValidator(final AciService validatorAciService, final IdolAnnotationsProcessorFactory processorFactory) {
        aciService = validatorAciService;
        this.processorFactory = processorFactory;
    }

    @Override
    public ValidationResult<?> validate(final QueryManipulation config) {
        return config.validate(aciService, processorFactory);
    }

    @Override
    public Class<QueryManipulation> getSupportedClass() {
        return QueryManipulation.class;
    }
}
