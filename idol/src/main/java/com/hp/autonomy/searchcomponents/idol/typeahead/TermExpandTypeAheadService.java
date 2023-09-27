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

package com.hp.autonomy.searchcomponents.idol.typeahead;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadConstants;
import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadService;
import com.hp.autonomy.searchcomponents.idol.annotations.IdolService;
import com.opentext.idol.types.marshalling.ProcessorFactory;
import com.opentext.idol.types.responses.TermExpandResponseData;
import com.hp.autonomy.types.requests.idol.actions.term.TermActions;
import com.hp.autonomy.types.requests.idol.actions.term.params.ExpandTypeParam;
import com.hp.autonomy.types.requests.idol.actions.term.params.ExpansionParam;
import com.hp.autonomy.types.requests.idol.actions.term.params.TermExpandParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.hp.autonomy.searchcomponents.idol.typeahead.IdolTypeAheadService.TERM_EXPAND_TYPE_AHEAD_SERVICE_BEAN_NAME;

/**
 * Term expand implementation of {@link TypeAheadService}
 */
@Service(TERM_EXPAND_TYPE_AHEAD_SERVICE_BEAN_NAME)
@IdolService
class TermExpandTypeAheadService implements IdolTypeAheadService {
    private final AciService contentAciService;
    private final Processor<TermExpandResponseData> processor;

    @Autowired
    TermExpandTypeAheadService(
            final AciService contentAciService,
            final ProcessorFactory processorFactory
    ) {
        this.contentAciService = contentAciService;
        processor = processorFactory.getResponseDataProcessor(TermExpandResponseData.class);
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
