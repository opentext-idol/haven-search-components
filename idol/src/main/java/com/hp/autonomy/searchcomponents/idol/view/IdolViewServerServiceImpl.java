/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.view;


import com.autonomy.aci.client.services.AciConstants;
import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.AciServiceException;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.configuration.server.ServerConfig;
import com.hp.autonomy.searchcomponents.core.view.ViewServerService;
import com.hp.autonomy.searchcomponents.idol.annotations.IdolService;
import com.hp.autonomy.searchcomponents.idol.search.HavenSearchAciParameterHandler;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewCapable;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewConfig;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewingMode;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import com.hp.autonomy.types.idol.marshalling.processors.CopyResponseProcessor;
import com.hp.autonomy.types.idol.responses.DocContent;
import com.hp.autonomy.types.idol.responses.GetContentResponseData;
import com.hp.autonomy.types.idol.responses.Hit;
import com.hp.autonomy.types.requests.idol.actions.connector.ConnectorActions;
import com.hp.autonomy.types.requests.idol.actions.connector.params.ConnectorViewParams;
import com.hp.autonomy.types.requests.idol.actions.query.QueryActions;
import com.hp.autonomy.types.requests.idol.actions.view.ViewActions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static com.hp.autonomy.searchcomponents.core.view.ViewServerService.VIEW_SERVER_SERVICE_BEAN_NAME;

/**
 * Default Idol implementation of {@link ViewServerService}
 */
@Service(VIEW_SERVER_SERVICE_BEAN_NAME)
@IdolService
class IdolViewServerServiceImpl implements IdolViewServerService {
    private final AciService contentAciService;
    private final AciService viewAciService;
    private final HavenSearchAciParameterHandler parameterHandler;
    private final Processor<GetContentResponseData> getContentResponseProcessor;
    private final ConfigService<? extends ViewCapable> configService;

    @Autowired
    IdolViewServerServiceImpl(final AciService contentAciService, final AciService viewAciService, final ProcessorFactory processorFactory, final HavenSearchAciParameterHandler parameterHandler, final ConfigService<? extends ViewCapable> configService) {
        this.contentAciService = contentAciService;
        this.viewAciService = viewAciService;
        this.parameterHandler = parameterHandler;
        this.configService = configService;

        getContentResponseProcessor = processorFactory.getResponseDataProcessor(GetContentResponseData.class);
    }

    /**
     * Provides an HTML rendering of the given IDOL document reference. This first performs a GetContent to make sure the
     * document exists, then reads the configured reference field and passes the value of the field to ViewServer.
     *
     * @param request      options
     * @param outputStream The ViewServer output
     * @throws ViewDocumentNotFoundException If the given document reference does not exist in IDOL
     * @throws ViewNoReferenceFieldException If the document with the given reference does not have the required reference field
     * @throws ReferenceFieldBlankException  If the configured reference field name is blank
     * @throws ViewServerErrorException      If ViewServer returns a status code outside the 200 range
     */
    @Override
    public void viewDocument(final IdolViewRequest request, final OutputStream outputStream) throws ViewDocumentNotFoundException, ViewNoReferenceFieldException, ReferenceFieldBlankException {
        final String reference = getReferenceFieldValue(request.getDocumentReference(), request.getDatabase());

        final AciParameters viewParameters = new AciParameters(ViewActions.View.name());
        parameterHandler.addViewParameters(viewParameters, reference, request);

        try {
            viewAciService.executeAction(viewParameters, new CopyResponseProcessor(outputStream));
        } catch (final AciServiceException e) {
            throw new ViewServerErrorException(request.getDocumentReference(), e);
        }
    }

    @Override
    public void viewStaticContentPromotion(final String documentReference, final OutputStream outputStream) throws IOException, AciErrorException {
        throw new NotImplementedException("Viewing static content promotions on premise is not yet possible");
    }

    private String getReferenceFieldValue(final String documentReference, final String database) throws ReferenceFieldBlankException, ViewDocumentNotFoundException, ViewNoReferenceFieldException {
        final ViewConfig viewConfig = configService.getConfig().getViewConfig();
        final String referenceField = viewConfig.getReferenceField();

        final ViewingMode viewingMode = viewConfig.getViewingMode();

        // fail fast if there's a misconfiguration
        if (viewingMode == ViewingMode.FIELD && StringUtils.isEmpty(referenceField)) {
            throw new ReferenceFieldBlankException();
        }

        // do a GetContent to check for document visibility and to read out required fields
        final AciParameters parameters = new AciParameters(QueryActions.GetContent.name());
        parameterHandler.addGetContentOutputParameters(parameters, database, documentReference, referenceField);

        final GetContentResponseData queryResponse;
        try {
            queryResponse = contentAciService.executeAction(parameters, getContentResponseProcessor);
        } catch (final AciErrorException e) {
            throw new ViewDocumentNotFoundException(documentReference, e);
        }

        final List<Hit> documents = queryResponse.getHits();
        if (documents.isEmpty()) {
            throw new ViewDocumentNotFoundException(documentReference);
        }

        return getReference(documentReference, viewConfig, referenceField, viewingMode, documents);
    }

    private String getReference(final String documentReference, final ViewConfig viewConfig, final String referenceField, final ViewingMode viewingMode, final List<Hit> documents) {
        final String reference;

        switch (viewingMode) {
            case CONNECTOR:
                final String identifier = parseFieldValue(AUTN_IDENTIFIER, documents);
                final String group = parseFieldValue(AUTN_GROUP, documents);
                final ServerConfig connectorConfig = viewConfig.getConnector();

                try {
                    final URI uri = new URIBuilder()
                            .setScheme(connectorConfig.getProtocol().name().toLowerCase())
                            .setHost(connectorConfig.getHost())
                            .setPort(connectorConfig.getPort())
                            // need to set the path because of ACI's weird format
                            .setPath("/")
                            .addParameter(AciConstants.PARAM_ACTION, ConnectorActions.View.name())
                            .addParameter(ConnectorViewParams.Identifier.name(), identifier)
                            .addParameter(ConnectorViewParams.Autn_Group.name(), group)
                            .build();

                    reference = uri.toString();
                } catch (final URISyntaxException e) {
                    // this should never happen
                    throw new ConnectorUriSyntaxException("Error constructing Connector URI", e);
                }
                break;
            case FIELD:
            default:
                reference = parseFieldValue(referenceField, documents);
                if (reference == null) {
                    throw new ViewNoReferenceFieldException(documentReference, referenceField);
                }
        }

        return reference;
    }

    private String parseFieldValue(final String referenceField, final List<Hit> documents) {
        final Hit document = documents.get(0);

        String referenceFieldValue = null;
        // Assume the field names are case insensitive
        final DocContent documentContent = document.getContent();
        if (documentContent != null && CollectionUtils.isNotEmpty(documentContent.getContent())) {
            final NodeList fields = ((Node) documentContent.getContent().get(0)).getChildNodes();
            for (int i = 0; i < fields.getLength(); i++) {
                final Node field = fields.item(i);
                if (field.getLocalName().equalsIgnoreCase(referenceField)) {
                    referenceFieldValue = field.getFirstChild() == null ? null : field.getFirstChild().getNodeValue();
                    break;
                }
            }
        }
        return referenceFieldValue;
    }

}
