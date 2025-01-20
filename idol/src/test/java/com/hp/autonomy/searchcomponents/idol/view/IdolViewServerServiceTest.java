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

package com.hp.autonomy.searchcomponents.idol.view;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.AciServiceException;
import com.autonomy.aci.client.util.ActionParameters;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.view.raw.RawContentViewer;
import com.hp.autonomy.searchcomponents.core.view.raw.RawDocument;
import com.hp.autonomy.searchcomponents.idol.search.HavenSearchAciParameterHandler;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewCapable;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewConfig;
import com.opentext.idol.types.marshalling.ProcessorFactory;
import com.opentext.idol.types.responses.DocContent;
import com.opentext.idol.types.responses.GetContentResponseData;
import com.opentext.idol.types.responses.Hit;
import com.opentext.idol.types.responses.QueryResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IdolViewServerServiceTest {
    private static final String SAMPLE_REFERENCE_FIELD_NAME = "URL";

    @Mock
    private AciService contentAciService;

    @Mock
    private AciService viewAciService;

    @Mock
    private ProcessorFactory processorFactory;

    @Mock
    private HavenSearchAciParameterHandler parameterHandler;

    @Mock
    private ConfigService<ViewCapable> configService;

    @Mock
    private ViewCapable viewCapableConfig;

    @Mock
    private IdolViewRequest request;

    @Mock
    private RawContentViewer rawContentViewer;

    private IdolViewServerService idolViewServerService;

    @BeforeEach
    public void setUp() {
        final ViewConfig viewConfig = ViewConfig.builder().referenceField(SAMPLE_REFERENCE_FIELD_NAME).build();
        Mockito.lenient().when(viewCapableConfig.getViewConfig()).thenReturn(viewConfig);
        Mockito.lenient().when(configService.getConfig()).thenReturn(viewCapableConfig);

        Mockito.lenient().when(request.getDocumentReference()).thenReturn("dede952d-8a4d-4f54-ac1f-5187bf10a744");
        Mockito.lenient().when(request.getHighlightExpression()).thenReturn("SomeText");

        Mockito.lenient().when(rawContentViewer.formatRawContent(any())).then(invocation -> {
            return IOUtils.toInputStream("raw_content", StandardCharsets.UTF_8);
        });

        idolViewServerService = new IdolViewServerServiceImpl(contentAciService, viewAciService, processorFactory, parameterHandler, configService, rawContentViewer);
    }

    @Test
    public void viewDocument() throws ViewDocumentNotFoundException, IOException {
        final GetContentResponseData responseData = mockResponseData();
        when(contentAciService.executeAction(any(ActionParameters.class), any())).thenReturn(responseData);
        idolViewServerService.viewDocument(request, mock(OutputStream.class));

        verify(parameterHandler).addViewParameters(any(), eq("http://en.wikipedia.org/wiki/Car"), any());
        verify(viewAciService).executeAction(any(), any());
    }

    @Test
    public void viewStaticContentPromotion() throws IOException {
        Assertions.assertThrows(NotImplementedException.class, () -> {
            idolViewServerService.viewStaticContentPromotion("SomeReference", mock(OutputStream.class));
        });
    }

    @Test
    public void noReferenceFieldConfigured() throws IOException {
        final GetContentResponseData responseData = mockResponseData();
        when(contentAciService.executeAction(any(ActionParameters.class), any())).thenReturn(responseData);
        when(viewCapableConfig.getViewConfig()).thenReturn(ViewConfig.builder().build());

        idolViewServerService.viewDocument(mock(IdolViewRequest.class), mock(OutputStream.class));
    }

    @Test
    public void errorGettingContent() throws IOException {
        when(contentAciService.executeAction(any(ActionParameters.class), any())).thenThrow(new AciErrorException());
        Assertions.assertThrows(ViewDocumentNotFoundException.class, () -> {
            idolViewServerService.viewDocument(request, mock(OutputStream.class));
        });
    }

    @Test
    public void noDocumentFound() throws IOException {
        when(contentAciService.executeAction(any(ActionParameters.class), any())).thenReturn(new GetContentResponseData());
        Assertions.assertThrows(ViewDocumentNotFoundException.class, () -> {
            idolViewServerService.viewDocument(request, mock(OutputStream.class));
        });
    }

    @Test
    public void noMatchingField() throws IOException {
        final QueryResponse responseData = new GetContentResponseData();

        final Hit hit = new Hit();

        final String title = "The Title";
        hit.setTitle(title);

        final String reference = "the_reference";
        hit.setReference(reference);

        responseData.getHits().add(hit);

        final DocContent docContent = new DocContent();
        hit.setContent(docContent);

        final Node node = mock(Node.class);
        docContent.getContent().add(node);

        final Node contentTextNode = mock(Node.class);
        final String content = "The document content";
        when(contentTextNode.getNodeValue()).thenReturn(content);

        final Node contentNode = mock(Node.class);
        when(contentNode.getLocalName()).thenReturn("DRECONTENT");
        when(contentNode.getFirstChild()).thenReturn(contentTextNode);

        final NodeList childNodes = mock(NodeList.class);
        when(childNodes.getLength()).thenReturn(1);
        when(childNodes.item(eq(0))).thenReturn(contentNode);
        when(node.getChildNodes()).thenReturn(childNodes);

        when(contentAciService.executeAction(any(ActionParameters.class), any())).thenReturn(responseData);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        idolViewServerService.viewDocument(request, outputStream);
        final String output = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);

        assertThat(output, is("raw_content"));

        final RawDocument expectedRawDocument = RawDocument.builder()
                .content(content)
                .title(title)
                .reference(reference)
                .build();

        verify(rawContentViewer).formatRawContent(eq(expectedRawDocument));
    }

    @Test
    public void viewServer404() throws IOException {
        final GetContentResponseData responseData = mockResponseData();
        when(contentAciService.executeAction(any(ActionParameters.class), any())).thenReturn(responseData);
        when(viewAciService.executeAction(any(ActionParameters.class), any())).thenThrow(new AciServiceException());

        Assertions.assertThrows(ViewServerErrorException.class, () -> {
            idolViewServerService.viewDocument(request, mock(OutputStream.class));
        });
    }

    private GetContentResponseData mockResponseData() {
        final GetContentResponseData responseData = new GetContentResponseData();

        final Hit hit = new Hit();
        responseData.getHits().add(hit);

        final DocContent content = new DocContent();
        hit.setContent(content);

        final Node node = mock(Node.class);
        content.getContent().add(node);

        final NodeList childNodes = mock(NodeList.class);
        when(childNodes.getLength()).thenReturn(1);
        when(node.getChildNodes()).thenReturn(childNodes);

        final Node referenceNode = mock(Node.class);
        when(referenceNode.getLocalName()).thenReturn(SAMPLE_REFERENCE_FIELD_NAME);

        final Node textNode = mock(Node.class);
        Mockito.lenient().when(textNode.getNodeValue()).thenReturn("http://en.wikipedia.org/wiki/Car");
        Mockito.lenient().when(referenceNode.getFirstChild()).thenReturn(textNode);
        when(childNodes.item(0)).thenReturn(referenceNode);

        return responseData;
    }
}
