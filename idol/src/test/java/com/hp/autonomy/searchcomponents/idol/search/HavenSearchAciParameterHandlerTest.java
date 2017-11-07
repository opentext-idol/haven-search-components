/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.transport.AciParameter;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.configuration.authentication.CommunityPrincipal;
import com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.configuration.QueryManipulation;
import com.hp.autonomy.searchcomponents.idol.view.IdolViewRequest;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import com.hp.autonomy.types.requests.idol.actions.query.params.SummaryParam;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HavenSearchAciParameterHandlerTest {
    @Mock
    private ConfigService<IdolSearchCapable> configService;

    @Mock
    private DocumentFieldsService documentFieldsService;

    @Mock
    private AuthenticationInformationRetriever<?, CommunityPrincipal> authenticationInformationRetriever;

    @Mock
    private IdolQueryRestrictions queryRestrictions;

    @Mock
    private IdolSearchRequest searchRequest;

    @Mock
    private IdolGetContentRequestIndex indexAndReferences;

    @Mock
    private IdolViewRequest viewRequest;

    private HavenSearchAciParameterHandler parameterHandler;

    private AciParameters aciParameters;

    @Before
    public void setUp() {
        aciParameters = new AciParameters();
        parameterHandler = new HavenSearchAciParameterHandlerImpl(configService, documentFieldsService, authenticationInformationRetriever);
    }

    @Test
    public void addSearchRestrictions() {
        when(queryRestrictions.getQueryText()).thenReturn("Some Text");
        when(queryRestrictions.getFieldText()).thenReturn("Some field text");
        when(queryRestrictions.getDatabases()).thenReturn(Collections.singletonList("Database1"));
        when(queryRestrictions.getStateMatchIds()).thenReturn(Collections.singletonList("stateID1"));
        when(queryRestrictions.getStateDontMatchIds()).thenReturn(Collections.singletonList("stateID2"));
        when(queryRestrictions.getMaxDate()).thenReturn(ZonedDateTime.now());
        when(queryRestrictions.isAnyLanguage()).thenReturn(true);
        parameterHandler.addSearchRestrictions(aciParameters, queryRestrictions);
        assertThat(aciParameters, hasSize(10));
    }

    @Test
    public void addSearchOutputParameters() {
        when(searchRequest.getStart()).thenReturn(1);
        when(searchRequest.getMaxResults()).thenReturn(50);
        when(searchRequest.getSummary()).thenReturn(SummaryParam.Concept.name());
        when(searchRequest.getSummaryCharacters()).thenReturn(250);
        when(searchRequest.isHighlight()).thenReturn(true);
        when(searchRequest.getPrint()).thenReturn(PrintParam.Fields.name());
        when(searchRequest.getPrintFields()).thenReturn(Arrays.asList("CATEGORY", "REFERENCE"));
        parameterHandler.addSearchOutputParameters(aciParameters, searchRequest);
        assertThat(aciParameters, hasSize(14));
    }

    @Test
    public void addGetDocumentOutputParameters() {
        when(indexAndReferences.getIndex()).thenReturn("Database1");
        when(indexAndReferences.getReferences()).thenReturn(Collections.singleton("SomeReference"));
        parameterHandler.addGetDocumentOutputParameters(aciParameters, indexAndReferences, PrintParam.Fields);
        assertThat(aciParameters, hasSize(11));
    }

    @Test
    public void addGetContentOutputParameters() {
        parameterHandler.addGetContentOutputParameters(aciParameters, "Database1", "ref", "field");
        assertThat(aciParameters, hasSize(3));
    }

    @Test
    public void addLanguageType() {
        when(queryRestrictions.getLanguageType()).thenReturn("englishUTF8");
        parameterHandler.addLanguageRestriction(aciParameters, queryRestrictions);
        assertThat(aciParameters, hasItem(new AciParameter(QueryParams.LanguageType.name(), "englishUTF8")));
    }

    @Test
    public void defaultLanguageType() {
        parameterHandler.addLanguageRestriction(aciParameters, queryRestrictions);
        assertThat(aciParameters, Matchers.empty());
    }

    @Test
    public void noLanguageRestriction() {
        when(queryRestrictions.isAnyLanguage()).thenReturn(true);
        parameterHandler.addLanguageRestriction(aciParameters, queryRestrictions);
        assertThat(aciParameters, hasItem(new AciParameter(QueryParams.AnyLanguage.name(), true)));
    }

    @Test
    public void addQmsParameters() {
        final IdolSearchCapable config = mock(IdolSearchCapable.class);
        when(config.getQueryManipulation()).thenReturn(QueryManipulation.builder().blacklist("ISO_BLACKLIST").expandQuery(true).synonymDatabaseMatch(true).build());
        when(configService.getConfig()).thenReturn(config);
        parameterHandler.addQmsParameters(aciParameters, null);
        assertThat(aciParameters, hasSize(3));
    }

    @Test
    public void addStoreStateParameters() {
        parameterHandler.addStoreStateParameters(aciParameters);
        assertThat(aciParameters, hasSize(2));
    }

    @Test
    public void addViewParameters() {
        when(viewRequest.getHighlightExpression()).thenReturn("SomeText");
        parameterHandler.addViewParameters(aciParameters, "123456", viewRequest);
        assertThat(aciParameters, hasSize(11));
    }
}
