/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search.fields;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldType;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.hod.configuration.HodSearchCapable;
import com.hp.autonomy.searchcomponents.hod.search.HodSearchResult;
import com.hp.autonomy.types.requests.Documents;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HodSearchResultDeserializerTest {
    private static String sampleJson;

    @BeforeClass
    public static void init() throws IOException {
        sampleJson = IOUtils.toString(HodSearchResultDeserializerTest.class.getResourceAsStream("/sampleHodQueryResponse.json"));
    }

    @Mock
    private ConfigService<? extends HodSearchCapable> configService;

    @Mock
    private HodSearchCapable config;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        final SimpleModule customModule = new SimpleModule();
        customModule.addDeserializer(HodSearchResult.class, new HodSearchResultDeserializer(configService));
        objectMapper.registerModule(customModule);
        objectMapper.registerModule(new JodaModule());

        final Set<FieldInfo<?>> customFields = new HashSet<>();
//        customFields.add(new FieldInfo<DateTime>("date_modified", "Last Modified Date", FieldType.DATE));
        final FieldsInfo fieldsInfo = new FieldsInfo.Builder()
                .populateResponseMap("modifiedDate", new FieldInfo<DateTime>("modifiedDate", "modified_date", "Last Modified Date", FieldType.DATE))
                .populateResponseMap("links", new FieldInfo<String>("links", "links", null, FieldType.STRING))
                .build();
        when(config.getFieldsInfo()).thenReturn(fieldsInfo);
        when(configService.getConfig()).thenReturn(config);
    }

    @Test
    public void deserialization() throws IOException {
        final List<HodSearchResult> documents = deserialize();
        assertThat(documents, is(not(empty())));
        final HodSearchResult firstResult = documents.get(0);
        assertThat(firstResult.getFieldMap().get("links").getValues(), is(not(empty())));
        assertThat(firstResult.getFieldMap().keySet(), hasSize(2));
    }

    @Test
    public void serialization() throws IOException {
        final List<HodSearchResult> documents = deserialize();
        final String json = objectMapper.writeValueAsString(documents);
        assertNotNull(json);
    }

    private List<HodSearchResult> deserialize() throws IOException {
        final JavaType javaType = objectMapper.getTypeFactory().constructParametrizedType(Documents.class, Documents.class, HodSearchResult.class);
        @SuppressWarnings("unchecked") final Documents<HodSearchResult> results = objectMapper.readValue(sampleJson, javaType);
        return results.getDocuments();
    }
}
