/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.typeahead;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadConstants;
import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadService;
import com.hp.autonomy.types.idol.TermExpandResponseData;
import com.hp.autonomy.types.requests.idol.actions.term.TermActions;
import com.hp.autonomy.types.requests.idol.actions.term.params.ExpandTypeParam;
import com.hp.autonomy.types.requests.idol.actions.term.params.ExpansionParam;
import com.hp.autonomy.types.requests.idol.actions.term.params.TermExpandParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
@Service
public class TermExpandTypeAheadService implements TypeAheadService<AciErrorException> {
    private final AciService contentAciService;
    private final Processor<TermExpandResponseData> processor;

    @Autowired
    public TermExpandTypeAheadService(
            final AciService contentAciService,
            final AciResponseJaxbProcessorFactory processorFactory
    ) {
        this.contentAciService = contentAciService;
        processor = processorFactory.createAciResponseProcessor(TermExpandResponseData.class);
    }

    @Override
    public List<String> getSuggestions(final String text) {
        final AciParameters parameters = new AciParameters(TermActions.TermExpand.name());
        parameters.put(TermExpandParams.Expansion.name(), ExpansionParam.Wild);
        parameters.put(TermExpandParams.Stemming.name(), false);
        parameters.put(TermExpandParams.MaxTerms.name(), TypeAheadConstants.MAX_RESULTS);
        parameters.put(TermExpandParams.Type.name(), ExpandTypeParam.DocOccs);
        parameters.put(TermExpandParams.Text.name(), text);

        final TermExpandResponseData response = contentAciService.executeAction(parameters, processor);

        return response.getTerm().stream().map(term -> term.getValue().toLowerCase()).collect(Collectors.toCollection(LinkedList::new));
    }
}
