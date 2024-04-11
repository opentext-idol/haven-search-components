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

package com.hp.autonomy.searchcomponents.idol.databases;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.ActionParameters;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesService;
import com.hp.autonomy.searchcomponents.idol.annotations.IdolService;
import com.hp.autonomy.types.requests.idol.actions.status.StatusActions;
import com.opentext.idol.types.marshalling.ProcessorFactory;
import com.opentext.idol.types.responses.Database;
import com.opentext.idol.types.responses.GetStatusResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Idol databases service implementation: retrieves public database information by running GetStatus against content engine and parsing the response
 */
@Service(DatabasesService.DATABASES_SERVICE_BEAN_NAME)
@IdolService
class IdolDatabasesServiceImpl implements IdolDatabasesService {
    private final AciService contentAciService;
    private final Processor<GetStatusResponseData> responseProcessor;

    @Autowired
    public IdolDatabasesServiceImpl(final AciService contentAciService, final ProcessorFactory processorFactory) {
        this.contentAciService = contentAciService;

        responseProcessor = processorFactory.getResponseDataProcessor(GetStatusResponseData.class);
    }

    @Override
    public Set<Database> getDatabases(final IdolDatabasesRequest request) throws AciErrorException {
        final GetStatusResponseData responseData = contentAciService.executeAction(new ActionParameters(StatusActions.GetStatus.name()), responseProcessor);
        final List<Database> allDatabases = responseData.getDatabases().getDatabase();

        return allDatabases.stream().filter(database -> !database.isInternal()).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
