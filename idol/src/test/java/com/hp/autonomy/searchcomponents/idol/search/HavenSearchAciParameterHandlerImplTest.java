/*
 * Copyright 2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.configuration.authentication.CommunityPrincipal;
import com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HavenSearchAciParameterHandlerImplTest {
    private HavenSearchAciParameterHandler handler;
    @Mock
    private ConfigService<IdolSearchCapable> configService;
    @Mock
    private DocumentFieldsService documentFieldService;
    @Mock
    private AuthenticationInformationRetriever<?, CommunityPrincipal> authenticationInformationRetriever;
    @Mock
    private IdolQueryRestrictions queryRestrictions;

    private AciParameters aciParameters;

    @Before
    public void setUp() {
        handler = new HavenSearchAciParameterHandlerImpl(configService, documentFieldService, authenticationInformationRetriever, null, null);

        when(configService.getConfig()).thenReturn(mock(IdolSearchCapable.class));

        aciParameters = new AciParameters();
    }

    @Test
    public void testAddSearchRestrictions() {
        final ZonedDateTime minDate = ZonedDateTime.of(2010, 11, 11, 2, 2, 2, 110, ZoneOffset.UTC);
        final ZonedDateTime maxDate = ZonedDateTime.of(2017, 11, 11, 2, 2, 2, 110, ZoneOffset.UTC);

        setUpQueryRestrictions(minDate, maxDate);

        handler.addSearchRestrictions(aciParameters, queryRestrictions);

        assertThat("2010-11-11T02:02:02Z",
                   is(DateTimeFormatter.ISO_INSTANT.format(minDate.truncatedTo(ChronoUnit.SECONDS))));
        assertThat("2017-11-11T02:02:02Z",
                   is(DateTimeFormatter.ISO_INSTANT.format(maxDate.truncatedTo(ChronoUnit.SECONDS))));
    }

    @Test
    public void testAddSearchRestrictionsInOneBC() {
        final ZonedDateTime minDate = ZonedDateTime.of(-1, 11, 11, 2, 2, 2, 110, ZoneOffset.UTC);
        final ZonedDateTime maxDate = ZonedDateTime.of(0, 11, 11, 2, 2, 2, 110, ZoneOffset.UTC);

        setUpQueryRestrictions(minDate, maxDate);

        handler.addSearchRestrictions(aciParameters, queryRestrictions);

        assertThat("-0001-11-11T02:02:02Z",
                   is(DateTimeFormatter.ISO_INSTANT.format(minDate.truncatedTo(ChronoUnit.SECONDS))));
        assertThat("0000-11-11T02:02:02Z",
                   is(DateTimeFormatter.ISO_INSTANT.format(maxDate.truncatedTo(ChronoUnit.SECONDS))));
    }

    @Test
    public void testAddSearchRestrictionsWithBCDate() {
        final ZonedDateTime minDate = ZonedDateTime.of(-43, 11, 11, 2, 2, 2, 110, ZoneOffset.UTC);
        final ZonedDateTime maxDate = ZonedDateTime.of(-11, 11, 11, 2, 2, 2, 110, ZoneOffset.UTC);

        setUpQueryRestrictions(minDate, maxDate);

        handler.addSearchRestrictions(aciParameters, queryRestrictions);

        assertThat("-0043-11-11T02:02:02Z",
                   is(DateTimeFormatter.ISO_INSTANT.format(minDate.truncatedTo(ChronoUnit.SECONDS))));
        assertThat("-0011-11-11T02:02:02Z",
                   is(DateTimeFormatter.ISO_INSTANT.format(maxDate.truncatedTo(ChronoUnit.SECONDS))));
    }

    private void setUpQueryRestrictions(final ZonedDateTime minDate, final ZonedDateTime maxDate) {
        when(queryRestrictions.getQueryText()).thenReturn("query text");
        when(queryRestrictions.getDatabases()).thenReturn(Collections.singletonList("database-name"));
        when(queryRestrictions.getStateMatchIds()).thenReturn(Collections.singletonList("state-match-ID"));
        when(queryRestrictions.getStateDontMatchIds()).thenReturn(Collections.singletonList("state-dont-match-ID"));

        when(queryRestrictions.getMinDate()).thenReturn(minDate);
        when(queryRestrictions.getMaxDate()).thenReturn(maxDate);
    }
}
