/*
 * Copyright 2015-2017 Open Text.
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

package com.hp.autonomy.searchcomponents.idol.search.fields;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldType;
import com.hp.autonomy.searchcomponents.core.config.FieldValue;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.core.fields.FieldDisplayNameGenerator;
import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import com.hp.autonomy.searchcomponents.core.search.PromotionCategory;
import com.hp.autonomy.searchcomponents.core.test.CoreTestContext;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.search.IdolSearchResult;
import com.hp.autonomy.types.idol.responses.DocContent;
import com.hp.autonomy.types.idol.responses.Hit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.*;

import static com.hp.autonomy.searchcomponents.core.test.CoreTestContext.CORE_CLASSES_PROPERTY;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings({"unused", "SpringJavaAutowiredMembersInspection"})
@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {CoreTestContext.class, FieldsParserImpl.class, IdolDocumentFieldsServiceImpl.class},
    properties = CORE_CLASSES_PROPERTY,
    webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class FieldsParserTest {
    @MockBean
    private ConfigService<IdolSearchCapable> configService;
    @MockBean
    private IdolSearchCapable config;
    @Autowired
    private FieldPathNormaliser fieldPathNormaliser;
    @MockBean
    private FieldDisplayNameGenerator fieldDisplayNameGenerator;
    @Autowired
    private FieldsParser fieldsParser;

    @Before
    public void setUp() {
        final FieldsInfo fieldsInfo = FieldsInfo.builder()
            .populateResponseMap("Custom Date", FieldInfo.<ZonedDateTime>builder()
                .id("Custom Date")
                .name(fieldPathNormaliser.normaliseFieldPath("DOCUMENT/CUSTOM_DATE"))
                .type(FieldType.DATE)
                .advanced(true)
                .build())
            .populateResponseMap("author", FieldInfo.<String>builder()
                .id("author")
                .name(fieldPathNormaliser.normaliseFieldPath("DOCUMENT/CUSTOM_ARRAY"))
                .build())
            .populateResponseMap("complex", FieldInfo.<Serializable>builder()
                .id("complex")
                .name(fieldPathNormaliser.normaliseFieldPath("DOCUMENT/CUSTOM_RECORD"))
                .type(FieldType.RECORD)
                .build())
            .build();
        when(config.getFieldsInfo()).thenReturn(fieldsInfo);
        when(configService.getConfig()).thenReturn(config);
    }

    @Test
    public void parseDocumentFields() {
        final IdolSearchResult.IdolSearchResultBuilder builder = IdolSearchResult.builder();
        fieldsParser.parseDocumentFields(mockHit(), builder);
        final IdolSearchResult idolSearchResult = builder.build();
        final Map<String, FieldInfo<?>> fieldMap = idolSearchResult.getFieldMap();

        assertNotNull(fieldMap.get("Custom Date"));

        assertEquals(
            Arrays.asList(new FieldValue<>("a", null), new FieldValue<>("b", null)),
            fieldMap.get("author").getValues());

        final HashMap<String, List<String>> recordValue = new HashMap<>();
        recordValue.put("first", Collections.singletonList("val 1"));
        recordValue.put("second", Collections.singletonList("val 2"));
        assertEquals(
            Collections.singletonList(new FieldValue<>(recordValue, null)),
            fieldMap.get("complex").getValues());
    }

    @Test
    public void parseStaticContentPromotionResult() {
        final IdolSearchResult.IdolSearchResultBuilder builder = IdolSearchResult.builder();
        final Hit hit = mockHit();
        hit.setPromotionname("SomeName");
        fieldsParser.parseDocumentFields(hit, builder);
        final IdolSearchResult idolSearchResult = builder.build();
        assertEquals(PromotionCategory.STATIC_CONTENT_PROMOTION, idolSearchResult.getPromotionCategory());
    }

    @Test
    public void parseCardinalPlacementPromotionResult() {
        final IdolSearchResult.IdolSearchResultBuilder builder = IdolSearchResult.builder();
        final Hit hit = mockInjectedPromotionHit();
        fieldsParser.parseDocumentFields(hit, builder);
        final IdolSearchResult idolSearchResult = builder.build();
        assertEquals(PromotionCategory.CARDINAL_PLACEMENT, idolSearchResult.getPromotionCategory());
    }

    private Hit mockHit() {
        final Hit hit = new Hit();
        hit.setTitle("Some Title");

        final DocContent content = new DocContent();
        final Element element = mock(Element.class);
        content.getContent().add(element);

        when(element.getNodeName()).thenReturn("DOCUMENT");
        when(element.hasChildNodes()).thenReturn(true);
        final NodeList childNodes = mock(NodeList.class);
        when(childNodes.getLength()).thenReturn(5);
        mockNodeListEntry(childNodes, 0, "CUSTOM_DATE", "2016-02-03T11:42:00Z");
        mockNodeListEntry(childNodes, 1, "CUSTOM_ARRAY", "a");
        mockNodeListEntry(childNodes, 2, "CUSTOM_ARRAY", "b");
        mockNodeListEntry(childNodes, 3, "UNKNOWN", "c");

        final Element recordElement = mock(Element.class);
        when(recordElement.getNodeName()).thenReturn("CUSTOM_RECORD");
        when(recordElement.hasChildNodes()).thenReturn(true);
        final NodeList recordChildNodes = mock(NodeList.class);
        when(recordChildNodes.getLength()).thenReturn(2);
        mockNodeListEntry(recordChildNodes, 0, "FIRST", "val 1");
        mockNodeListEntry(recordChildNodes, 1, "SECOND", "val 2");
        when(recordElement.getChildNodes()).thenReturn(recordChildNodes);

        when(childNodes.item(4)).thenReturn(recordElement);
        when(element.getChildNodes()).thenReturn(childNodes);

        mockHardCodedField(element, IdolDocumentFieldsServiceImpl.QMS_ID_FIELD, "123");
        when(element.getElementsByTagName(IdolDocumentFieldsServiceImpl.INJECTED_PROMOTION_FIELD)).thenReturn(mock(NodeList.class));

        hit.setContent(content);

        return hit;
    }

    private void mockHardCodedField(final Element element, final String name, @SuppressWarnings("SameParameterValue") final String value) {
        final NodeList childNodes = mock(NodeList.class);
        when(childNodes.getLength()).thenReturn(1);
        mockNodeListEntry(childNodes, 0, name, value);
        when(element.getElementsByTagName(name)).thenReturn(childNodes);
    }

    private void mockNodeListEntry(final NodeList nodes, final int i, final String name, final String value) {
        final Element namedNode = mock(Element.class);
        when(namedNode.getNodeName()).thenReturn(name);
        final Text textNode = mock(Text.class);
        when(textNode.getNodeValue()).thenReturn(value);
        final NodeList textNodes = mock(NodeList.class);
        when(namedNode.getChildNodes()).thenReturn(textNodes);
        when(textNodes.getLength()).thenReturn(1);
        when(textNodes.item(0)).thenReturn(textNode);
        when(namedNode.getFirstChild()).thenReturn(textNode);
        when(nodes.item(i)).thenReturn(namedNode);
    }

    private Hit mockInjectedPromotionHit() {
        final Hit hit = new Hit();
        hit.setTitle("Some Other Title");

        final DocContent content = new DocContent();
        final Element element = mock(Element.class);
        when(element.hasChildNodes()).thenReturn(true);
        when(element.getChildNodes()).thenReturn(mock(NodeList.class));

        final NodeList childNodes = mock(NodeList.class);
        when(childNodes.getLength()).thenReturn(1);
        final String name = IdolDocumentFieldsServiceImpl.INJECTED_PROMOTION_FIELD.toUpperCase();
        mockNodeListEntry(childNodes, 0, name, "true");
        when(element.getElementsByTagName(anyString())).thenReturn(mock(NodeList.class));
        when(element.getElementsByTagName(name)).thenReturn(childNodes);
        content.getContent().add(element);
        hit.setContent(content);

        return hit;
    }
}
