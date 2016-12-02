/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.idol.databases.IdolDatabasesRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.databases.IdolDatabasesService;
import com.hp.autonomy.searchcomponents.idol.search.fields.FieldsParser;
import com.hp.autonomy.types.idol.responses.Database;
import com.hp.autonomy.types.idol.responses.Hit;
import com.hp.autonomy.types.idol.responses.QueryResponseData;
import com.hp.autonomy.types.requests.Documents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
@RunWith(MockitoJUnitRunner.class)
public class QueryResponseParserTest {
    @Mock
    private FieldsParser documentFieldsService;

    @Mock
    private IdolDatabasesService databasesService;

    @Mock
    private ObjectFactory<IdolDatabasesRequestBuilder> databasesRequestBuilderFactory;

    @Mock
    private IdolDatabasesRequestBuilder databasesRequestBuilder;

    @Mock
    private Function<AciParameters, QueryResponseData> queryExecutor;

    @Mock
    private IdolQueryRestrictions queryRestrictions;

    @Mock
    private IdolSearchRequest searchRequest;

    private QueryResponseParser queryResponseParser;

    @Before
    public void setUp() {
        when(databasesRequestBuilderFactory.getObject()).thenReturn(databasesRequestBuilder);

        when(queryRestrictions.getDatabases()).thenReturn(Arrays.asList("Database1", "Database2"));
        when(searchRequest.getQueryRestrictions()).thenReturn(queryRestrictions);

        queryResponseParser = new QueryResponseParserImpl(documentFieldsService, databasesService, databasesRequestBuilderFactory);
    }

    @Test
    public void parseResults() {
        final QueryResponseData responseData = mockQueryResponse();

        final Documents<IdolSearchResult> results = queryResponseParser.parseQueryResults(searchRequest, new AciParameters(), responseData, queryExecutor);
        assertThat(results.getDocuments(), is(not(empty())));
    }

    @Test
    public void autoCorrect() {
        final QueryResponseData responseData = mockQueryResponse();
        responseData.setSpellingquery("spelling");
        responseData.setSpelling("mm, mmh");

        when(queryExecutor.apply(any(AciParameters.class))).thenReturn(mockQueryResponse());

        final Documents<IdolSearchResult> results = queryResponseParser.parseQueryResults(searchRequest, new AciParameters(), responseData, queryExecutor);
        assertThat(results.getDocuments(), is(not(empty())));
    }

    @Test
    public void invalidDatabaseWarning() {
        final QueryResponseData responseData = mockQueryResponse();
        responseData.getWarning().add(QueryResponseParserImpl.MISSING_DATABASE_WARNING);

        final Database goodDatabase = new Database();
        goodDatabase.setName("Database2");
        when(databasesService.getDatabases(any())).thenReturn(Collections.singleton(goodDatabase));

        final Documents<IdolSearchResult> results = queryResponseParser.parseQueryResults(searchRequest, new AciParameters(), responseData, queryExecutor);
        assertThat(results.getDocuments(), is(not(empty())));
        assertNotNull(results.getWarnings());
        assertThat(results.getWarnings().getInvalidDatabases(), hasSize(1));
        assertEquals("Database1", results.getWarnings().getInvalidDatabases().iterator().next());
    }

    protected QueryResponseData mockQueryResponse() {
        final QueryResponseData responseData = new QueryResponseData();
        responseData.setTotalhits(1);
        final Hit hit = mockHit();
        responseData.getHits().add(hit);

        return responseData;
    }

    private Hit mockHit() {
        final Hit hit = new Hit();
        hit.setTitle("Some Title");
        return hit;
    }
}
