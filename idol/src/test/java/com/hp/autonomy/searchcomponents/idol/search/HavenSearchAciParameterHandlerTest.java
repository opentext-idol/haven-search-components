/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.transport.AciParameter;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.languages.LanguagesService;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import com.hp.autonomy.searchcomponents.idol.configuration.HavenSearchCapable;
import com.hp.autonomy.searchcomponents.idol.configuration.QueryManipulation;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HavenSearchAciParameterHandlerTest {
    @Mock
    private ConfigService<? extends HavenSearchCapable> configService;
    @Mock
    private LanguagesService languagesService;

    private HavenSearchAciParameterHandler parameterHandler;

    @Before
    public void setUp() {
        parameterHandler = new HavenSearchAciParameterHandlerImpl(configService, languagesService);
    }

    @Test
    public void addSearchRestrictions() {
        final AciParameters aciParameters = new AciParameters();
        final QueryRestrictions<String> queryRestrictions = new IdolQueryRestrictions.Builder().setQueryText("Some Text").setFieldText("Some field text").setDatabases(Collections.singletonList("Database1")).setMaxDate(DateTime.now()).setAnyLanguage(true).build();
        parameterHandler.addSearchRestrictions(aciParameters, queryRestrictions);
        assertThat(aciParameters, is(not(empty())));
    }

    @Test
    public void addSearchOutputParameters() {
        final AciParameters aciParameters = new AciParameters();
        final SearchRequest<String> searchRequest = new SearchRequest<>(null, 0, 50, "Context", null, true, false, null);
        parameterHandler.addSearchOutputParameters(aciParameters, searchRequest);
        assertThat(aciParameters, is(not(empty())));
    }

    @Test
    public void addLanguageRestriction() {
        final AciParameters aciParameters = new AciParameters();
        final QueryRestrictions<String> queryRestrictions = new IdolQueryRestrictions.Builder().setLanguageType("englishUTF8").build();
        parameterHandler.addLanguageRestriction(aciParameters, queryRestrictions);
        assertThat(aciParameters, hasItem(new AciParameter(QueryParams.LanguageType.name(), "englishUTF8")));
    }

    @Test
    public void addDefaultLanguageRestriction() {
        final AciParameters aciParameters = new AciParameters();
        final QueryRestrictions<String> queryRestrictions = new IdolQueryRestrictions.Builder().build();
        when(languagesService.getDefaultLanguageId()).thenReturn("englishUTF8");
        parameterHandler.addLanguageRestriction(aciParameters, queryRestrictions);
        assertThat(aciParameters, hasItem(new AciParameter(QueryParams.LanguageType.name(), "englishUTF8")));
    }

    @Test
    public void noLanguageRestriction() {
        final AciParameters aciParameters = new AciParameters();
        final QueryRestrictions<String> queryRestrictions = new IdolQueryRestrictions.Builder().setAnyLanguage(true).build();
        parameterHandler.addLanguageRestriction(aciParameters, queryRestrictions);
        assertThat(aciParameters, hasItem(new AciParameter(QueryParams.AnyLanguage.name(), true)));
    }

    @Test
    public void addQmsParameters() {
        final AciParameters aciParameters = new AciParameters();
        final HavenSearchCapable config = mock(HavenSearchCapable.class);
        when(config.getQueryManipulation()).thenReturn(new QueryManipulation.Builder().setBlacklist("ISO_BLACKLIST").setExpandQuery(true).build());
        when(configService.getConfig()).thenReturn(config);
        parameterHandler.addQmsParameters(aciParameters, null);
        assertThat(aciParameters, hasSize(2));
    }
}
