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

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.util.ActionParameters;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldValue;
import com.hp.autonomy.searchcomponents.idol.databases.IdolDatabasesRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.databases.IdolDatabasesService;
import com.hp.autonomy.searchcomponents.idol.search.fields.FieldsParser;
import com.hp.autonomy.types.requests.Documents;
import com.opentext.idol.types.responses.Database;
import com.opentext.idol.types.responses.Hit;
import com.opentext.idol.types.responses.QueryResponseData;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectFactory;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private Function<ActionParameters, QueryResponseData> queryExecutor;

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

        final Documents<IdolSearchResult> results = queryResponseParser.parseQueryResults(searchRequest, new ActionParameters(), responseData, queryExecutor);
        assertThat(results.getDocuments(), is(not(empty())));
    }

    @Test
    public void autoCorrect() {
        final QueryResponseData responseData = mockQueryResponse();
        responseData.setSpellingquery("spelling");
        responseData.setSpelling("mm, mmh");

        when(queryExecutor.apply(any(ActionParameters.class))).thenReturn(mockQueryResponse());

        final Documents<IdolSearchResult> results = queryResponseParser.parseQueryResults(searchRequest, new ActionParameters(), responseData, queryExecutor);
        assertThat(results.getDocuments(), is(not(empty())));
    }

    @Test
    public void invalidDatabaseWarning() {
        final QueryResponseData responseData = mockQueryResponse();
        responseData.getWarning().add(QueryResponseParserImpl.MISSING_DATABASE_WARNING);

        final Database goodDatabase = new Database();
        goodDatabase.setName("Database2");
        when(databasesService.getDatabases(any())).thenReturn(Collections.singleton(goodDatabase));

        final Documents<IdolSearchResult> results = queryResponseParser.parseQueryResults(searchRequest, new ActionParameters(), responseData, queryExecutor);
        assertThat(results.getDocuments(), is(not(empty())));
        assertNotNull(results.getWarnings());
        assertThat(results.getWarnings().getInvalidDatabases(), hasSize(1));
        assertEquals("Database1", results.getWarnings().getInvalidDatabases().iterator().next());
    }

    @Test
    public void parseQueryHits_referenceField() {
        final Map<String, List<Serializable>> fieldValues1 = new HashMap<>();
        fieldValues1.put("customRef", Collections.singletonList("customid1"));

        final List<IdolSearchResult> results = queryResponseParser.parseQueryHits(Arrays.asList(
            mockHit("defaultid1", fieldValues1),
            mockHit("defaultid2", Collections.emptyMap())
        ), "CUSTOMREF");

        assertThat(results, hasSize(2));
        assertThat(results.get(0).getReference(), is("customid1"));
        assertThat(results.get(1).getReference(), is("defaultid2"));
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

    private Hit mockHit(final String reference, final Map<String, List<Serializable>> fieldValues) {
        final Hit hit = new Hit();
        hit.setTitle("Some Title");
        hit.setReference(reference);

        final CaseInsensitiveMap<String, FieldInfo<?>> fieldMap = new CaseInsensitiveMap<>();
        for (final String name : fieldValues.keySet()) {
            final List<FieldValue<Serializable>> values = fieldValues.get(name).stream()
                .map(val -> new FieldValue<>(val, null))
                .collect(Collectors.toList());
            fieldMap.put(name, FieldInfo.builder().values(values).build());
        }
        Mockito.doAnswer(inv -> {
            ((IdolSearchResult.IdolSearchResultBuilder) inv.getArguments()[1]).fieldMap(fieldMap);
            return null;
        }).when(documentFieldsService).parseDocumentFields(eq(hit), any());

        return hit;
    }
}
