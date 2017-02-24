/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search.fields;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldType;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.core.fields.FieldDisplayNameGenerator;
import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import com.hp.autonomy.searchcomponents.core.test.CoreTestContext;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

import static com.hp.autonomy.searchcomponents.core.test.CoreTestContext.CORE_CLASSES_PROPERTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@SuppressWarnings("unused")
@RunWith(SpringRunner.class)
@JsonTest
@AutoConfigureJsonTesters(enabled = false)
@SpringBootTest(
        classes = {CoreTestContext.class, HodSearchResultDeserializer.class},
        properties = CORE_CLASSES_PROPERTY,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class HodSearchResultDeserializerTest {
    private static String sampleJson;
    @MockBean
    private ConfigService<HodSearchCapable> configService;
    @Autowired
    private FieldPathNormaliser fieldPathNormaliser;
    @Autowired
    private FieldDisplayNameGenerator fieldDisplayNameGenerator;
    @Mock
    private HodSearchCapable config;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeClass
    public static void init() throws IOException {
        sampleJson = IOUtils.toString(HodSearchResultDeserializerTest.class.getResourceAsStream("/sampleHodQueryResponse.json"));
    }

    @Before
    public void setUp() {
        final FieldsInfo fieldsInfo = FieldsInfo.builder()
                .populateResponseMap("modifiedDate", FieldInfo.<DateTime>builder()
                        .id("modifiedDate")
                        .name(fieldPathNormaliser.normaliseFieldPath("modified_date"))
                        .name(fieldPathNormaliser.normaliseFieldPath("date_modified"))
                        .type(FieldType.DATE)
                        .advanced(true)
                        .build())
                .populateResponseMap("links", FieldInfo.<String>builder()
                        .id("links")
                        .name(fieldPathNormaliser.normaliseFieldPath("links"))
                        .advanced(true)
                        .build())
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
