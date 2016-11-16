/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.transport.AciParameter;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.configuration.authentication.CommunityPrincipal;
import com.hp.autonomy.searchcomponents.core.search.AciSearchRequest;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndex;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.configuration.QueryManipulation;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import com.hp.autonomy.types.requests.idol.actions.query.params.SummaryParam;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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

    private HavenSearchAciParameterHandler parameterHandler;

    private AciParameters aciParameters;

    @Before
    public void setUp() {
        aciParameters = new AciParameters();
        parameterHandler = new HavenSearchAciParameterHandlerImpl(configService, documentFieldsService, authenticationInformationRetriever);
    }

    @Test
    public void addSearchRestrictions() {
        final QueryRestrictions<String> queryRestrictions = IdolQueryRestrictions.builder()
                .queryText("Some Text")
                .fieldText("Some field text")
                .databases(Collections.singletonList("Database1"))
                .stateMatchId("stateID1")
                .stateDontMatchId("stateID2")
                .maxDate(DateTime.now())
                .anyLanguage(true)
                .build();
        parameterHandler.addSearchRestrictions(aciParameters, queryRestrictions);
        assertThat(aciParameters, hasSize(10));
    }

    @Test
    public void addSearchOutputParameters() {
        final AciSearchRequest<String> searchRequest = SearchRequest.<String>builder()
                .queryRestrictions(null)
                .start(1)
                .maxResults(50)
                .summary(SummaryParam.Concept.name())
                .summaryCharacters(250)
                .sort(null)
                .highlight(true)
                .autoCorrect(true)
                .print(PrintParam.Fields.name())
                .printFields(Arrays.asList("CATEGORY", "REFERENCE"))
                .queryType(null)
                .build();
        parameterHandler.addSearchOutputParameters(aciParameters, searchRequest);
        assertThat(aciParameters, hasSize(14));
    }

    @Test
    public void addGetDocumentOutputParameters() {
        final GetContentRequestIndex<String> indexAndReferences = new GetContentRequestIndex<>("Database1", Collections.singleton("SomeReference"));
        parameterHandler.addGetDocumentOutputParameters(aciParameters, indexAndReferences, PrintParam.Fields);
        assertThat(aciParameters, hasSize(11));
    }

    @Test
    public void addGetContentOutputParameters() {
        parameterHandler.addGetContentOutputParameters(aciParameters, "Database1", "ref", "field");
        assertThat(aciParameters, hasSize(5));
    }

    @Test
    public void addLanguageType() {
        final QueryRestrictions<String> queryRestrictions = IdolQueryRestrictions.builder().languageType("englishUTF8").build();
        parameterHandler.addLanguageRestriction(aciParameters, queryRestrictions);
        assertThat(aciParameters, hasItem(new AciParameter(QueryParams.LanguageType.name(), "englishUTF8")));
    }

    @Test
    public void defaultLanguageType() {
        final QueryRestrictions<String> queryRestrictions = IdolQueryRestrictions.builder().build();
        parameterHandler.addLanguageRestriction(aciParameters, queryRestrictions);
        assertThat(aciParameters, Matchers.empty());
    }

    @Test
    public void noLanguageRestriction() {
        final QueryRestrictions<String> queryRestrictions = IdolQueryRestrictions.builder().anyLanguage(true).build();
        parameterHandler.addLanguageRestriction(aciParameters, queryRestrictions);
        assertThat(aciParameters, hasItem(new AciParameter(QueryParams.AnyLanguage.name(), true)));
    }

    @Test
    public void addQmsParameters() {
        final IdolSearchCapable config = mock(IdolSearchCapable.class);
        when(config.getQueryManipulation()).thenReturn(QueryManipulation.builder().blacklist("ISO_BLACKLIST").expandQuery(true).build());
        when(configService.getConfig()).thenReturn(config);
        parameterHandler.addQmsParameters(aciParameters, null);
        assertThat(aciParameters, hasSize(2));
    }
}
