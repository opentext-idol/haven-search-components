/*
 * Copyright 2017 Open Text.
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

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.configuration.authentication.CommunityPrincipal;
import com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService;
import com.hp.autonomy.searchcomponents.core.view.ViewingPart;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.view.IdolViewRequest;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
    @Mock
    private IdolViewRequest viewRequest;

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

        assertThat(aciParameters.get("mindate"), is("2010-11-11T02:02:02Z"));
        assertThat(aciParameters.get("maxdate"), is("2017-11-11T02:02:02Z"));
    }

    @Test
    public void testAddSearchRestrictionsInOneBC() {
        final ZonedDateTime minDate = ZonedDateTime.of(-1, 11, 11, 2, 2, 2, 110, ZoneOffset.UTC);
        final ZonedDateTime maxDate = ZonedDateTime.of(0, 11, 11, 2, 2, 2, 110, ZoneOffset.UTC);

        setUpQueryRestrictions(minDate, maxDate);

        handler.addSearchRestrictions(aciParameters, queryRestrictions);

        assertThat(aciParameters.get("mindate"), is("-0001-11-11T02:02:02Z"));
        assertThat(aciParameters.get("maxdate"), is("0000-11-11T02:02:02Z"));
    }

    @Test
    public void testAddSearchRestrictionsWithBCDate() {
        final ZonedDateTime minDate = ZonedDateTime.of(-43, 11, 11, 2, 2, 2, 110, ZoneOffset.UTC);
        final ZonedDateTime maxDate = ZonedDateTime.of(-11, 11, 11, 2, 2, 2, 110, ZoneOffset.UTC);

        setUpQueryRestrictions(minDate, maxDate);

        handler.addSearchRestrictions(aciParameters, queryRestrictions);

        assertThat(aciParameters.get("mindate"), is("-0043-11-11T02:02:02Z"));
        assertThat(aciParameters.get("maxdate"), is("-0011-11-11T02:02:02Z"));
    }

    private void setUpQueryRestrictions(final ZonedDateTime minDate, final ZonedDateTime maxDate) {
        when(queryRestrictions.getQueryText()).thenReturn("query text");
        when(queryRestrictions.getDatabases()).thenReturn(Collections.singletonList("database-name"));
        when(queryRestrictions.getStateMatchIds()).thenReturn(Collections.singletonList("state-match-ID"));
        when(queryRestrictions.getStateDontMatchIds()).thenReturn(Collections.singletonList("state-dont-match-ID"));

        when(queryRestrictions.getMinDate()).thenReturn(minDate);
        when(queryRestrictions.getMaxDate()).thenReturn(maxDate);
    }

    @Test
    public void testAddViewParameters_document() {
        when(viewRequest.getPart()).thenReturn(ViewingPart.DOCUMENT);
        Mockito.lenient().when(viewRequest.getSubDocRef()).thenReturn("thesubdoc");
        handler.addViewParameters(aciParameters, "the doc ref", viewRequest);
        assertThat(aciParameters.get("noaci"), is("true"));
        assertThat(aciParameters.get("reference"), is("the doc ref"));
        assertThat(aciParameters.get("embedimages"), is("true"));
        assertThat(aciParameters.get("stripscript"), is("true"));
        assertThat(aciParameters.get("originalbaseurl"), is("true"));
        assertThat(aciParameters.get("outputtype"), is("HTML"));
        Assert.assertNull(aciParameters.get("urlprefix"));
        Assert.assertNull(aciParameters.get("linkspec"));
    }

    @Test
    public void testAddViewParameters_document_urlPrefix() {
        when(viewRequest.getPart()).thenReturn(ViewingPart.DOCUMENT);
        when(viewRequest.getUrlPrefix()).thenReturn("http://www.example.com/subdoc?subref=");
        handler.addViewParameters(aciParameters, "the doc ref", viewRequest);
        Assert.assertThat(aciParameters.get("urlprefix"), is("http://www.example.com/subdoc?subref="));
    }

    @Test
    public void testAddViewParameters_original() {
        when(viewRequest.getPart()).thenReturn(ViewingPart.ORIGINAL);
        handler.addViewParameters(aciParameters, "the doc ref", viewRequest);
        assertThat(aciParameters.get("noaci"), is("true"));
        assertThat(aciParameters.get("reference"), is("the doc ref"));
        Assert.assertNull(aciParameters.get("embedimages"));
        Assert.assertNull(aciParameters.get("stripscript"));
        Assert.assertNull(aciParameters.get("originalbaseurl"));
        assertThat(aciParameters.get("outputtype"), is("Raw"));
        Assert.assertNull(aciParameters.get("urlprefix"));
        Assert.assertNull(aciParameters.get("linkspec"));
    }

    @Test
    public void testAddViewParameters_subdocument() {
        when(viewRequest.getPart()).thenReturn(ViewingPart.SUBDOCUMENT);
        when(viewRequest.getSubDocRef()).thenReturn("thesubdoc");
        handler.addViewParameters(aciParameters, "the doc ref", viewRequest);
        assertThat(aciParameters.get("noaci"), is("true"));
        assertThat(aciParameters.get("reference"), is("the doc ref"));
        assertThat(aciParameters.get("embedimages"), is("true"));
        assertThat(aciParameters.get("stripscript"), is("true"));
        assertThat(aciParameters.get("originalbaseurl"), is("true"));
        assertThat(aciParameters.get("outputtype"), is("HTML"));
        Assert.assertNull(aciParameters.get("urlprefix"));
        assertThat(aciParameters.get("linkspec"), is("thesubdoc"));
    }

    @Test
    public void testAddViewParameters_subdocument_urlPrefix() {
        when(viewRequest.getPart()).thenReturn(ViewingPart.SUBDOCUMENT);
        when(viewRequest.getUrlPrefix()).thenReturn("http://www.example.com/subdoc?subref=");
        handler.addViewParameters(aciParameters, "the doc ref", viewRequest);
        Assert.assertThat(aciParameters.get("urlprefix"), is("http://www.example.com/subdoc?subref="));
    }

}
