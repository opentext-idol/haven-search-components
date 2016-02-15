/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.transport.AciParameter;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesService;
import com.hp.autonomy.searchcomponents.core.languages.LanguagesService;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndex;
import com.hp.autonomy.searchcomponents.core.search.PromotionCategory;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import com.hp.autonomy.searchcomponents.core.search.SuggestRequest;
import com.hp.autonomy.searchcomponents.idol.configuration.HavenSearchCapable;
import com.hp.autonomy.searchcomponents.idol.configuration.QueryManipulation;
import com.hp.autonomy.searchcomponents.idol.databases.IdolDatabasesRequest;
import com.hp.autonomy.types.idol.Database;
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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

    @Mock
    protected DatabasesService<Database, IdolDatabasesRequest, AciErrorException> databasesService;

    protected IdolDocumentService idolDocumentService;

    @Before
    public void setUp() {
        when(havenSearchConfig.getQueryManipulation()).thenReturn(new QueryManipulation.Builder().build());
        when(configService.getConfig()).thenReturn(havenSearchConfig);

        final FieldsParser fieldsParser = new FieldsParserImpl();
        final QueryResponseParser queryResponseParser = new QueryResponseParserImpl(fieldsParser, databasesService);
        idolDocumentService = new IdolDocumentService(configService, parameterHandler, queryResponseParser, contentAciService, qmsAciService, aciResponseProcessorFactory);
    }

    @Test
    public void queryContent() {
        final QueryResponseData responseData = mockQueryResponse();
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final Documents<IdolSearchResult> results = idolDocumentService.queryTextIndex(mockQueryParams());
        assertThat(results.getDocuments(), is(not(empty())));
    }

    @Test
    public void queryQms() {
        when(havenSearchConfig.getQueryManipulation()).thenReturn(new QueryManipulation.Builder().setEnabled(true).build());
        final QueryResponseData responseData = mockQueryResponse();
        when(qmsAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final Documents<IdolSearchResult> results = idolDocumentService.queryTextIndex(mockQueryParams());
        assertThat(results.getDocuments(), is(not(empty())));
    }

    @Test
    public void queryContentForPromotions() {
        final Documents<IdolSearchResult> results = idolDocumentService.queryTextIndexForPromotions(mockQueryParams());
        assertThat(results.getDocuments(), is(empty()));
    }

    @Test
    public void autoCorrect() {
        final QueryResponseData responseData = mockQueryResponse();
        responseData.setSpellingquery("spelling");
        responseData.setSpelling("mm, mmh");
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final QueryRestrictions<String> queryRestrictions = new IdolQueryRestrictions.Builder().setQueryText("*").setDatabases(Arrays.asList("Database1", "Database2")).setMaxDate(DateTime.now()).build();
        final SearchRequest<String> searchRequest = new SearchRequest<>(queryRestrictions, 0, 50, null, 250, null, true, true, null);
        final Documents<IdolSearchResult> results = idolDocumentService.queryTextIndex(searchRequest);
        assertThat(results.getDocuments(), is(not(empty())));
    }

    @Test
    public void invalidDatabaseWarning() {
        final QueryResponseData responseData = mockQueryResponse();
        responseData.getWarning().add(QueryResponseParserImpl.MISSING_DATABASE_WARNING);
        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final Database goodDatabase = new Database();
        goodDatabase.setName("Database2");
        when(databasesService.getDatabases(any(IdolDatabasesRequest.class))).thenReturn(Collections.singleton(goodDatabase));

        final QueryRestrictions<String> queryRestrictions = new IdolQueryRestrictions.Builder().setQueryText("*").setDatabases(Arrays.asList("Database1", "Database2")).setMaxDate(DateTime.now()).build();
        final SearchRequest<String> searchRequest = new SearchRequest<>(queryRestrictions, 0, 50, null, 250, null, true, true, null);
        final Documents<IdolSearchResult> results = idolDocumentService.queryTextIndex(searchRequest);
        assertThat(results.getDocuments(), is(not(empty())));
        assertNotNull(results.getWarnings());
        assertThat(results.getWarnings().getInvalidDatabases(), hasSize(1));
        assertEquals("Database1", results.getWarnings().getInvalidDatabases().iterator().next());
    }

    @Test
    public void queryQmsForPromotions() {
        when(havenSearchConfig.getQueryManipulation()).thenReturn(new QueryManipulation.Builder().setEnabled(true).build());
        final QueryResponseData responseData = new QueryResponseData();
        responseData.setTotalhits(2);
        final Hit hit1 = mockHit();
        hit1.setPromotionname("SomeName");
        final Hit hit2 = mockInjectedPromotionHit();
        responseData.getHit().addAll(Arrays.asList(hit1, hit2));
        when(qmsAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final Documents<IdolSearchResult> results = idolDocumentService.queryTextIndexForPromotions(mockQueryParams());
        assertThat(results.getDocuments(), is(not(empty())));
        assertEquals(PromotionCategory.STATIC_CONTENT_PROMOTION, results.getDocuments().get(0).getPromotionCategory());
        assertEquals(PromotionCategory.CARDINAL_PLACEMENT, results.getDocuments().get(1).getPromotionCategory());

    }

    @Test
    public void findSimilar() {
        final SuggestResponseData responseData = new SuggestResponseData();
        responseData.setTotalhits(1);
        final Hit hit = mockHit();
        responseData.getHit().add(hit);

        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final SuggestRequest<String> suggestRequest = new SuggestRequest<>("Some reference", new IdolQueryRestrictions.Builder().build(), 1, 30, "context", 250, "relevance", true);
        final Documents<IdolSearchResult> results = idolDocumentService.findSimilar(suggestRequest);
        assertThat(results.getDocuments(), is(not(empty())));
    }

    @Test
    public void getContent() {
        final QueryResponseData responseData = new QueryResponseData();
        responseData.setTotalhits(1);
        final Hit hit = mockHit();
        responseData.getHit().add(hit);

        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final GetContentRequest<String> getContentRequest = new GetContentRequest<>(Collections.singleton(new GetContentRequestIndex<>("Database1", Collections.singleton("Some reference"))));
        final List<IdolSearchResult> results = idolDocumentService.getDocumentContent(getContentRequest);
        assertThat(results, hasSize(1));
    }

    @Test
    public void getStateToken() {
        final String mockStateToken = "abc";
        final QueryResponseData responseData = new QueryResponseData();
        responseData.setState(mockStateToken);

        when(contentAciService.executeAction(anySetOf(AciParameter.class), any(Processor.class))).thenReturn(responseData);

        final String stateToken = idolDocumentService.getStateToken(mockQueryParams().getQueryRestrictions(), 42);
        assertEquals(mockStateToken, stateToken);
    }

    protected SearchRequest<String> mockQueryParams() {
        final QueryRestrictions<String> queryRestrictions = new IdolQueryRestrictions.Builder().setQueryText("*").setDatabases(Arrays.asList("Database1", "Database2")).setMaxDate(DateTime.now()).build();
        return new SearchRequest<>(queryRestrictions, 0, 50, null, 250, null, true, false, null);
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
        when(textNode.getNodeValue()).thenReturn("2016-02-03T11:42:00");
        when(childNode.getFirstChild()).thenReturn(textNode);
        when(childNodes.item(0)).thenReturn(childNode);
        when(element.getElementsByTagName(anyString())).thenReturn(childNodes);
        content.getContent().add(element);
        hit.setContent(content);

        return hit;
    }

    private Hit mockInjectedPromotionHit() {
        final Hit hit = new Hit();
        hit.setTitle("Some Other Title");

        final DocContent content = new DocContent();
        final Element element = mock(Element.class);
        final NodeList childNodes = mock(NodeList.class);
        when(childNodes.getLength()).thenReturn(1);
        final Element childNode = mock(Element.class);
        final Node textNode = mock(Node.class);
        when(textNode.getNodeValue()).thenReturn("true");
        when(childNode.getFirstChild()).thenReturn(textNode);
        when(childNodes.item(0)).thenReturn(childNode);
        when(element.getElementsByTagName(anyString())).thenReturn(mock(NodeList.class));
        when(element.getElementsByTagName(IdolSearchResult.INJECTED_PROMOTION_FIELD.toUpperCase())).thenReturn(childNodes);
        content.getContent().add(element);
        hit.setContent(content);

        return hit;
    }
}
