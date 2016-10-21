/*
 * Copyright 2015-2016 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.configuration;

import com.autonomy.aci.client.services.AciService;
import com.hp.autonomy.frontend.configuration.validation.ValidationResult;
import com.hp.autonomy.frontend.configuration.validation.Validator;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueryManipulationValidator implements Validator<QueryManipulation> {

    private final AciService aciService;
    private final ProcessorFactory processorFactory;

    @Autowired
    public QueryManipulationValidator(final AciService validatorAciService, final ProcessorFactory processorFactory) {
        aciService = validatorAciService;
        this.processorFactory = processorFactory;
    }

    @Override
    public ValidationResult<?> validate(final QueryManipulation config) {
        return config.getServer().validate(aciService, null, processorFactory);
    }

    @Override
    public Class<QueryManipulation> getSupportedClass() {
        return QueryManipulation.class;
    }
}
