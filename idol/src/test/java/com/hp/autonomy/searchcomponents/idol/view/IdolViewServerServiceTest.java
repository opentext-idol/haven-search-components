/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.view;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.AciServiceException;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.idol.search.HavenSearchAciParameterHandler;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewCapable;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewConfig;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import com.hp.autonomy.types.idol.responses.DocContent;
import com.hp.autonomy.types.idol.responses.GetContentResponseData;
import com.hp.autonomy.types.idol.responses.Hit;
import com.hp.autonomy.types.idol.responses.QueryResponse;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.OutputStream;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
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

    private IdolViewServerService idolViewServerService;

    @Before
    public void setUp() {
        final ViewConfig viewConfig = ViewConfig.builder().referenceField(SAMPLE_REFERENCE_FIELD_NAME).build();
        when(viewCapableConfig.getViewConfig()).thenReturn(viewConfig);
        when(configService.getConfig()).thenReturn(viewCapableConfig);

        idolViewServerService = new IdolViewServerService(contentAciService, viewAciService, processorFactory, parameterHandler, configService);
    }

    @Test
    public void viewDocument() throws ViewNoReferenceFieldException, ViewDocumentNotFoundException, ReferenceFieldBlankException {
        final GetContentResponseData responseData = mockResponseData();
        when(contentAciService.executeAction(any(AciParameters.class), any())).thenReturn(responseData);

        final IdolViewRequest request = IdolViewRequest.builder()
                .documentReference("dede952d-8a4d-4f54-ac1f-5187bf10a744")
                .database("SomeDatabase")
                .highlightExpression("SomeText")
                .build();
        idolViewServerService.viewDocument(request, mock(OutputStream.class));

        verify(parameterHandler).addViewParameters(any(), eq("http://en.wikipedia.org/wiki/Car"), any());
        verify(viewAciService).executeAction(any(), any());
    }

    @Test(expected = NotImplementedException.class)
    public void viewStaticContentPromotion() throws IOException {
        idolViewServerService.viewStaticContentPromotion("SomeReference", mock(OutputStream.class));
    }

    @Test(expected = ReferenceFieldBlankException.class)
    public void noReference() throws ViewNoReferenceFieldException, ViewDocumentNotFoundException, ReferenceFieldBlankException {
        when(viewCapableConfig.getViewConfig()).thenReturn(ViewConfig.builder().build());
        idolViewServerService.viewDocument(IdolViewRequest.builder().build(), mock(OutputStream.class));
    }

    @Test(expected = ViewDocumentNotFoundException.class)
    public void errorGettingContent() throws ViewNoReferenceFieldException, ViewDocumentNotFoundException, ReferenceFieldBlankException {
        when(contentAciService.executeAction(any(AciParameters.class), any(Processor.class))).thenThrow(new AciErrorException());
        final IdolViewRequest request = IdolViewRequest.builder()
                .documentReference("dede952d-8a4d-4f54-ac1f-5187bf10a744")
                .build();
        idolViewServerService.viewDocument(request, mock(OutputStream.class));
    }

    @Test(expected = ViewDocumentNotFoundException.class)
    public void noDocumentFound() throws ViewNoReferenceFieldException, ViewDocumentNotFoundException, ReferenceFieldBlankException {
        when(contentAciService.executeAction(any(AciParameters.class), any())).thenReturn(new GetContentResponseData());
        final IdolViewRequest request = IdolViewRequest.builder()
                .documentReference("dede952d-8a4d-4f54-ac1f-5187bf10a744")
                .build();
        idolViewServerService.viewDocument(request, mock(OutputStream.class));
    }

    @Test(expected = ViewNoReferenceFieldException.class)
    public void noMatchingField() throws ViewNoReferenceFieldException, ViewDocumentNotFoundException, ReferenceFieldBlankException {
        final QueryResponse responseData = new GetContentResponseData();

        final Hit hit = new Hit();
        responseData.getHits().add(hit);

        final DocContent content = new DocContent();
        hit.setContent(content);

        final Node node = mock(Node.class);
        content.getContent().add(node);

        final NodeList childNodes = mock(NodeList.class);
        when(childNodes.getLength()).thenReturn(0);
        when(node.getChildNodes()).thenReturn(childNodes);

        when(contentAciService.executeAction(any(AciParameters.class), any())).thenReturn(responseData);
        final IdolViewRequest request = IdolViewRequest.builder()
                .documentReference("dede952d-8a4d-4f54-ac1f-5187bf10a744")
                .build();
        idolViewServerService.viewDocument(request, mock(OutputStream.class));
    }

    @Test(expected = ViewServerErrorException.class)
    public void viewServer404() {
        final GetContentResponseData responseData = mockResponseData();
        when(contentAciService.executeAction(any(AciParameters.class), any())).thenReturn(responseData);
        when(viewAciService.executeAction(any(AciParameters.class), any(Processor.class))).thenThrow(new AciServiceException());

        final IdolViewRequest request = IdolViewRequest.builder()
                .documentReference("dede952d-8a4d-4f54-ac1f-5187bf10a744")
                .database("SomeDatabase")
                .build();
        idolViewServerService.viewDocument(request, mock(OutputStream.class));
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
        when(textNode.getNodeValue()).thenReturn("http://en.wikipedia.org/wiki/Car");
        when(referenceNode.getFirstChild()).thenReturn(textNode);
        when(childNodes.item(0)).thenReturn(referenceNode);

        return responseData;
    }
}