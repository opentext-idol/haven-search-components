/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.transport.AciParameter;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.searchcomponents.core.languages.LanguagesService;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.searchcomponents.idol.configuration.HavenSearchCapable;
import com.hp.autonomy.searchcomponents.idol.configuration.QueryManipulation;
import com.hp.autonomy.types.idol.DocContent;
import com.hp.autonomy.types.idol.Hit;
import com.hp.autonomy.types.idol.QueryResponseData;
import com.hp.autonomy.types.idol.SuggestResponseData;
import com.hp.autonomy.types.requests.Documents;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IdolDocumentServiceTest {
    @Mock
    protected HavenSearchAciParameterHandler parameterHandler;

    @Mock
    protected HavenSearchCapable havenSearchConfig;

    @Mock
    protected LanguagesService languagesService;

    @Mock
    protected ConfigService<HavenSearchCapable> configService;

    @Mock
    protected AciService contentAciService;

    @Mock
    protected AciService qmsAciService;

    @Mock
    protected AciResponseJaxbProcessorFactory aciResponseProcessorFactory;

    protected IdolDocumentService idolDocumentService;

    @Before
    public void setUp() {
        when(havenSearchConfig.getQueryManipulation()).thenReturn(new QueryManipulation.Builder().build());
        when(configService.getConfig()).thenReturn(havenSearchConfig);
        idolDocumentService = new IdolDocumentService(configService, parameterHandler, contentAciService, qmsAciService, aciResponseProcessorFactory);
    }

    @Test
    public void queryContent() {
        final QueryResponseData responseData = mockQueryResponse();
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final Documents<SearchResult> results = idolDocumentService.queryTextIndex(mockQueryParams());
        assertThat(results.getDocuments(), is(not(empty())));
    }

    @Test
    public void queryQms() {
        when(havenSearchConfig.getQueryManipulation()).thenReturn(new QueryManipulation.Builder().setEnabled(true).build());
        final QueryResponseData responseData = mockQueryResponse();
        when(qmsAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final Documents<SearchResult> results = idolDocumentService.queryTextIndex(mockQueryParams());
        assertThat(results.getDocuments(), is(not(empty())));
    }

    @Test
    public void queryContentForPromotions() {
        final Documents<SearchResult> results = idolDocumentService.queryTextIndexForPromotions(mockQueryParams());
        assertThat(results.getDocuments(), is(empty()));
    }

    @Test
    public void queryQmsForPromotions() {
        when(havenSearchConfig.getQueryManipulation()).thenReturn(new QueryManipulation.Builder().setEnabled(true).build());
        final QueryResponseData responseData = mockQueryResponse();
        when(qmsAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final Documents<SearchResult> results = idolDocumentService.queryTextIndexForPromotions(mockQueryParams());
        assertThat(results.getDocuments(), is(not(empty())));
    }

    @Test
    public void findSimilar() {
        final SuggestResponseData responseData = new SuggestResponseData();
        responseData.setTotalhits(1);
        final Hit hit = mockHit();
        responseData.getHit().add(hit);

        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final List<SearchResult> results = idolDocumentService.findSimilar(Collections.singleton("Database1"), "Some reference");
        assertThat(results, is(not(empty())));
    }

    protected SearchRequest<String> mockQueryParams() {
        final QueryRestrictions<String> queryRestrictions = new IdolQueryRestrictions.Builder().setQueryText("*").setDatabases(Arrays.asList("Database1", "Database2")).setMaxDate(DateTime.now()).build();
        return new SearchRequest<>(queryRestrictions, 0, 50, null, null, true, null);
    }

    protected QueryResponseData mockQueryResponse() {
        final QueryResponseData responseData = new QueryResponseData();
        responseData.setTotalhits(1);
        final Hit hit = mockHit();
        responseData.getHit().add(hit);

        return responseData;
    }

    private Hit mockHit() {
        final Hit hit = new Hit();
        hit.setTitle("Some Title");

        final DocContent content = new DocContent();
        final Element element = mock(Element.class);
        final NodeList childNodes = mock(NodeList.class);
        when(childNodes.getLength()).thenReturn(1);
        final Element childNode = mock(Element.class);
        final Node textNode = mock(Node.class);
        when(textNode.getNodeValue()).thenReturn("Some Value");
        when(childNode.getFirstChild()).thenReturn(textNode);
        when(childNodes.item(0)).thenReturn(childNode);
        when(element.getElementsByTagName(anyString())).thenReturn(childNodes);
        content.getContent().add(element);
        hit.setContent(content);

        return hit;
    }
}
