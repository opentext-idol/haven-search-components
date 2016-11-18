/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.typeahead;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadConstants;
import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadService;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import com.hp.autonomy.types.idol.responses.TypeAheadResponseData;
import com.hp.autonomy.types.requests.qms.actions.typeahead.TypeAheadActions;
import com.hp.autonomy.types.requests.qms.actions.typeahead.params.ModeParam;
import com.hp.autonomy.types.requests.qms.actions.typeahead.params.TypeAheadParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

import static com.hp.autonomy.searchcomponents.idol.typeahead.IdolTypeAheadConstants.QMS_TYPE_AHEAD_SERVICE_BEAN_NAME;

/**
 * Qms implementation of {@link TypeAheadService}
 */
@Service(QMS_TYPE_AHEAD_SERVICE_BEAN_NAME)
class QmsTypeAheadService implements TypeAheadService<AciErrorException> {
    private final ConfigService<? extends IdolSearchCapable> configService;
    private final AciService qmsAciService;
    private final Processor<TypeAheadResponseData> processor;

    @Autowired
    QmsTypeAheadService(
            final ConfigService<? extends IdolSearchCapable> configService,
            final AciService qmsAciService,
            final ProcessorFactory processorFactory
    ) {
        this.configService = configService;
        this.qmsAciService = qmsAciService;
        processor = processorFactory.getResponseDataProcessor(TypeAheadResponseData.class);
    }

    @Override
    public List<String> getSuggestions(final String text) {
        final ModeParam mode = configService.getConfig().getQueryManipulation().getTypeAheadMode();

        final AciParameters parameters = new AciParameters(TypeAheadActions.TypeAhead.name());
        parameters.add(TypeAheadParams.Mode.name(), mode);
        parameters.add(TypeAheadParams.MaxResults.name(), TypeAheadConstants.MAX_RESULTS);
        parameters.add(TypeAheadParams.Text.name(), text);

        final TypeAheadResponseData response = qmsAciService.executeAction(parameters, processor);

        final List<String> output = new LinkedList<>();
        for (final TypeAheadResponseData.Expansion expansion : response.getExpansion()) {
            final String value = expansion.getValue();
            // Do not lower case dictionary suggestions (they are explicitly defined by the user)
            output.add(ModeParam.Index == mode ? value.toLowerCase() : value);
        }

        return output;
    }
}
