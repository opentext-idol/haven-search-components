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
import com.hp.autonomy.searchcomponents.core.config.FieldValue;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.core.fields.FieldDisplayNameGenerator;
import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import com.hp.autonomy.searchcomponents.core.search.PromotionCategory;
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
import java.util.Map;

import static com.hp.autonomy.searchcomponents.core.test.CoreTestContext.CORE_CLASSES_PROPERTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@SuppressWarnings({"unused", "SpringJavaAutowiredMembersInspection"})
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
                        .advanced(false)
                        .build())
                .populateResponseMap("authors", FieldInfo.<String>builder()
                        .id("authors")
                        .displayName("Chief Creative")
                        .name(fieldPathNormaliser.normaliseFieldPath("authors"))
                        .name(fieldPathNormaliser.normaliseFieldPath("writers"))
                        .value(new FieldValue<>("Trump", "Orange"))
                        .advanced(false)
                        .build())
                .populateResponseMap("collaborators", FieldInfo.<String>builder()
                        .id("collaborators")
                        .name(fieldPathNormaliser.normaliseFieldPath("collaborators"))
                        .advanced(true)
                        .build())
                .build();

        when(config.getFieldsInfo()).thenReturn(fieldsInfo);
        when(configService.getConfig()).thenReturn(config);
    }

    @Test
    public void deserialization() throws IOException {
        final List<HodSearchResult> documents = deserialize();
        assertThat(documents, hasSize(2));

        final HodSearchResult firstResult = documents.get(0);

        assertThat(firstResult.getReference(), is("http://en.wikipedia.org/wiki/Fiji"));
        assertThat(firstResult.getTitle(), is("Fiji"));
        assertThat(firstResult.getIndex(), is("wiki_eng"));
        assertThat(firstResult.getPromotionCategory(), is(PromotionCategory.NONE));

        final Map<String, FieldInfo<?>> fieldMap = firstResult.getFieldMap();
        assertThat(fieldMap.keySet(), hasSize(4));

        final FieldInfo<?> collaborators = fieldMap.get("collaborators");
        assertThat(collaborators, not(nullValue()));
        assertThat(collaborators.getDisplayName(), is("Collaborators"));
        assertThat(collaborators.isAdvanced(), is(true));
        assertThat(collaborators.getValues(), hasSize(1));

        final FieldInfo<?> modifiedDate = fieldMap.get("modifiedDate");
        assertThat(modifiedDate, not(nullValue()));
        assertThat(modifiedDate.getDisplayName(), is("Modifieddate"));
        assertThat(modifiedDate.isAdvanced(), is(false));
        assertThat(modifiedDate.getValues(), hasSize(1));

        final FieldInfo<?> authors = fieldMap.get("authors");
        assertThat(authors, not(nullValue()));
        assertThat(authors.getDisplayName(), is("Chief Creative"));
        assertThat(authors.isAdvanced(), is(false));
        assertThat(authors.getValues(), hasSize(3));

        final FieldInfo<?> contentType = fieldMap.get("content_type");
        assertThat(contentType, not(nullValue()));
        assertThat(contentType.getDisplayName(), is("Content Type"));
        assertThat(contentType.isAdvanced(), is(true));
        assertThat(contentType.getValues(), hasSize(1));
    }

    private List<HodSearchResult> deserialize() throws IOException {
        final JavaType javaType = objectMapper.getTypeFactory().constructParametrizedType(Documents.class, Documents.class, HodSearchResult.class);
        @SuppressWarnings("unchecked") final Documents<HodSearchResult> results = objectMapper.readValue(sampleJson, javaType);
        return results.getDocuments();
    }
}
