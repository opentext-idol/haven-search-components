/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search.fields;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.search.PromotionCategory;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.search.IdolSearchResult;
import com.hp.autonomy.types.idol.DocContent;
import com.hp.autonomy.types.idol.Hit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FieldsParserTest {
    @Mock
    private ConfigService<? extends IdolSearchCapable> configService;

    @Mock
    private IdolSearchCapable config;

    private FieldsParser fieldsParser;

    @Before
    public void setUp() {
        fieldsParser = new FieldsParserImpl(configService);

//        final FieldAssociations fieldAssociations = new FieldAssociations();
//        fieldAssociations.setAuthor("CUSTOM_ARRAY");
//        final Set<FieldInfo<?>> customFields = new HashSet<>();
//        customFields.add(new FieldInfo<DateTime>("CUSTOM_DATE", "A Date", FieldType.DATE));
//        customFields.add(new FieldInfo<String>("CUSTOM_ARRAY", "An Array", FieldType.STRING));
//        final FieldsInfo fieldsInfo = new FieldsInfo(fieldAssociations, customFields);
//        when(config.getFieldsInfo()).thenReturn(fieldsInfo);
//        when(configService.getConfig()).thenReturn(config);
    }

    @Test
    public void parseDocumentFields() {
        final IdolSearchResult.Builder builder = new IdolSearchResult.Builder();
        fieldsParser.parseDocumentFields(mockHit(), builder);
        final IdolSearchResult idolSearchResult = builder.build();
//        assertThat(idolSearchResult.getAuthors(), hasSize(2));
        final Map<String, FieldInfo<?>> fieldMap = idolSearchResult.getFieldMap();
        assertNotNull(fieldMap.get("CUSTOM_DATE"));
        assertThat(fieldMap.get("CUSTOM_ARRAY").getValues(), hasSize(2));
    }

    @Test
    public void parseStaticContentPromotionResult() {
        final IdolSearchResult.Builder builder = new IdolSearchResult.Builder();
        final Hit hit = mockHit();
        hit.setPromotionname("SomeName");
        fieldsParser.parseDocumentFields(hit, builder);
        final IdolSearchResult idolSearchResult = builder.build();
        assertEquals(PromotionCategory.STATIC_CONTENT_PROMOTION, idolSearchResult.getPromotionCategory());
    }

    @Test
    public void parseCardinalPlacementPromotionResult() {
        final IdolSearchResult.Builder builder = new IdolSearchResult.Builder();
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

        mockField(element, IdolDocumentFieldsService.QMS_ID_FIELD.toUpperCase(), "123");
        when(element.getElementsByTagName(IdolDocumentFieldsService.INJECTED_PROMOTION_FIELD.toUpperCase())).thenReturn(mock(NodeList.class));

        mockField(element, "CUSTOM_DATE", "2016-02-03T11:42:00");
        mockArrayField(element, "CUSTOM_ARRAY", "a", "b");

        when(element.hasChildNodes()).thenReturn(true);
        final NodeList childNodes = mock(NodeList.class);
        when(childNodes.getLength()).thenReturn(3);
        when(element.getChildNodes()).thenReturn(childNodes);

        hit.setContent(content);

        return hit;
    }

    private void mockField(final Element element, final String name, final String value) {
        final NodeList childNodes = mock(NodeList.class);
        when(childNodes.getLength()).thenReturn(1);
        mockNodeListEntry(childNodes, 0, name, value);
        when(element.getElementsByTagName(name)).thenReturn(childNodes);
    }

    private void mockArrayField(final Element element, final String name, final String... values) {
        final NodeList childNodes = mock(NodeList.class);
        when(childNodes.getLength()).thenReturn(values.length);
        for (int i = 0; i < values.length; i++) {
            mockNodeListEntry(childNodes, i, name, values[i]);
        }
        when(element.getElementsByTagName(name)).thenReturn(childNodes);
    }

    private void mockNodeListEntry(final NodeList nodes, final int i, final String name, final String value) {
        final Element childNode = mock(Element.class);
        when(childNode.getNodeName()).thenReturn(name);
        final Node textNode = mock(Node.class);
        when(textNode.getNodeValue()).thenReturn(value);
        when(childNode.getFirstChild()).thenReturn(textNode);
        when(nodes.item(i)).thenReturn(childNode);
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
        final String name = IdolDocumentFieldsService.INJECTED_PROMOTION_FIELD.toUpperCase();
        mockNodeListEntry(childNodes, 0, name, "true");
        when(element.getElementsByTagName(anyString())).thenReturn(mock(NodeList.class));
        when(element.getElementsByTagName(name)).thenReturn(childNodes);
        content.getContent().add(element);
        hit.setContent(content);

        return hit;
    }
}
