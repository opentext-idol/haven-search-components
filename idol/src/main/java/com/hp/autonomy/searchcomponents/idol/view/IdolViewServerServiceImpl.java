/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.idol.view;

import com.autonomy.aci.client.services.*;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.aci.content.printfields.PrintFields;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.configuration.server.ServerConfig;
import com.hp.autonomy.searchcomponents.core.view.ViewServerService;
import com.hp.autonomy.searchcomponents.core.view.raw.RawContentViewer;
import com.hp.autonomy.searchcomponents.core.view.raw.RawDocument;
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
import com.hp.autonomy.types.requests.idol.actions.query.params.GetContentParams;
import com.hp.autonomy.types.requests.idol.actions.query.params.HighlightParam;
import com.hp.autonomy.types.requests.idol.actions.view.ViewActions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static com.hp.autonomy.searchcomponents.core.view.ViewServerService.VIEW_SERVER_SERVICE_BEAN_NAME;

/**
 * Default Idol implementation of {@link ViewServerService}
 */
@Service(VIEW_SERVER_SERVICE_BEAN_NAME)
@IdolService
class IdolViewServerServiceImpl implements IdolViewServerService {
    private static final String CONTENT_FIELD = "DRECONTENT";
    private final AciService contentAciService;
    private final AciService viewAciService;
    private final HavenSearchAciParameterHandler parameterHandler;
    private final Processor<GetContentResponseData> getContentResponseProcessor;
    private final ConfigService<? extends ViewCapable> configService;
    private final RawContentViewer rawContentViewer;

    @Autowired
    IdolViewServerServiceImpl(
            final AciService contentAciService,
            final AciService viewAciService,
            final ProcessorFactory processorFactory,
            final HavenSearchAciParameterHandler parameterHandler,
            final ConfigService<? extends ViewCapable> configService,
            final RawContentViewer rawContentViewer
    ) {
        this.contentAciService = contentAciService;
        this.viewAciService = viewAciService;
        this.parameterHandler = parameterHandler;
        this.configService = configService;
        this.rawContentViewer = rawContentViewer;

        getContentResponseProcessor = processorFactory.getResponseDataProcessor(GetContentResponseData.class);
    }

    /**
     * Provides an HTML rendering of the given IDOL document reference. This first performs a GetContent to make sure the
     * document exists, then reads the configured reference field and passes the value of the field to ViewServer.
     *
     * @param request      options
     * @param outputStream The ViewServer output
     * @throws ViewDocumentNotFoundException If the given document reference does not exist in IDOL
     * @throws ViewServerErrorException      If ViewServer returns a status code outside the 200 range
     */
    @Override
    public void viewDocument(final IdolViewRequest request, final OutputStream outputStream) throws ViewDocumentNotFoundException, IOException {
        final ViewConfig viewConfig = configService.getConfig().getViewConfig();

        if (viewConfig.getViewingMode() == ViewingMode.UNIVERSAL) {
            final AciParameters viewParameters = new AciParameters(ViewActions.View.name());
            parameterHandler.addViewParameters(viewParameters, request.getDocumentReference(), request);

            try {
                viewAciService.executeAction(viewParameters, new CopyResponseProcessor(outputStream));
                return;
            } catch (final AciServiceException e) {
                throw new ViewServerErrorException(request.getDocumentReference(), e);
            }
        }

        // Only fetch the minimum necessary information to know if we should use View, Connector, or DRECONTENT rendering.
        final PrintFields printFields = new PrintFields(AUTN_IDENTIFIER, AUTN_GROUP);
        final String refField = viewConfig.getReferenceField();
        if(StringUtils.isNotBlank(refField)) {
            printFields.append(refField);
        }

        final Hit document = loadDocument(request.getDocumentReference(), request.getDatabase(), printFields, null);
        final Optional<String> maybeUrl = readViewUrl(document);

        if (maybeUrl.isPresent()) {
            final AciParameters viewParameters = new AciParameters(ViewActions.View.name());
            parameterHandler.addViewParameters(viewParameters, maybeUrl.get(), request);

            try {
                viewAciService.executeAction(viewParameters, new CopyResponseProcessor(outputStream));
            } catch (final AciServiceException e) {
                throw new ViewServerErrorException(request.getDocumentReference(), e);
            }
        } else {
            // We need to fetch the DRECONTENT if we have to use the DRECONTENT rendering fallback.
            final Hit docContent = loadDocument(request.getDocumentReference(), request.getDatabase(), new PrintFields(CONTENT_FIELD), request.getHighlightExpression());

            final String content = parseFieldValue(docContent, CONTENT_FIELD).orElse("");

            final RawDocument rawDocument = RawDocument.builder()
                    .reference(document.getReference())
                    .title(document.getTitle())
                    .content(content)
                    .build();

            try (final InputStream inputStream = rawContentViewer.formatRawContent(rawDocument)) {
                IOUtils.copy(inputStream, outputStream);
            }
        }
    }

    @Override
    public void viewStaticContentPromotion(final String documentReference, final OutputStream outputStream) throws IOException, AciErrorException {
        throw new NotImplementedException("Viewing static content promotions on premise is not yet possible");
    }

    private Hit loadDocument(final String documentReference, final String database, final PrintFields printFields, final String highlightExpression) {
        // do a GetContent to check for document visibility and to read out required fields
        final AciParameters parameters = new AciParameters(QueryActions.GetContent.name());
        parameters.add(GetContentParams.Print.name(), "Fields");
        parameters.add(GetContentParams.PrintFields.name(), printFields);
        parameterHandler.addGetContentOutputParameters(parameters, database, documentReference, null);

        if (StringUtils.isNotBlank(highlightExpression)) {
            parameters.add(GetContentParams.Highlight.name(), HighlightParam.Terms);
            parameters.add(GetContentParams.Links.name(), highlightExpression);
            parameters.add(GetContentParams.Boolean.name(), true);
            parameters.add(GetContentParams.StartTag.name(), HIGHLIGHT_START_TAG);
            parameters.add(GetContentParams.EndTag.name(), HIGHLIGHT_END_TAG);
        }

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

        return documents.get(0);
    }

    private Optional<String> readViewUrl(final Hit document) {
        final ViewConfig viewConfig = configService.getConfig().getViewConfig();
        final ViewingMode viewingMode = viewConfig.getViewingMode();

        if (viewingMode == ViewingMode.CONNECTOR) {
            final Optional<String> maybeIdentifier = parseFieldValue(document, AUTN_IDENTIFIER);
            final Optional<String> maybeGroup = parseFieldValue(document, AUTN_GROUP);

            if (maybeGroup.isPresent() && maybeIdentifier.isPresent()) {
                final ServerConfig connectorConfig = viewConfig.getConnector();

                try {
                    final URI uri = new URIBuilder()
                            .setScheme(connectorConfig.getProtocol().name().toLowerCase())
                            .setHost(connectorConfig.getHost())
                            .setPort(connectorConfig.getPort())
                            // need to set the path because of ACI's weird format
                            .setPath("/")
                            .addParameter(AciConstants.PARAM_ACTION, ConnectorActions.View.name())
                            .addParameter(ConnectorViewParams.AnyLanguage.name(), "true")
                            .addParameter(ConnectorViewParams.Identifier.name(), maybeIdentifier.get())
                            .addParameter(ConnectorViewParams.Autn_Group.name(), maybeGroup.get())
                            .build();

                    return Optional.of(uri.toString());
                } catch (final URISyntaxException e) {
                    // this should never happen
                    throw new ConnectorUriSyntaxException("Error constructing Connector URI", e);
                }
            } else {
                return Optional.empty();
            }
        } else {
            final String referenceField = viewConfig.getReferenceField();
            return parseFieldValue(document, referenceField);
        }
    }

    private Optional<String> parseFieldValue(final Hit document, final String fieldName) {
        final DocContent documentContent = document.getContent();

        if (documentContent != null && CollectionUtils.isNotEmpty(documentContent.getContent())) {
            final NodeList fields = ((Node) documentContent.getContent().get(0)).getChildNodes();

            for (int i = 0; i < fields.getLength(); i++) {
                final Node fieldNode = fields.item(i);

                // Assume the field names are case insensitive
                if (fieldNode.getLocalName().equalsIgnoreCase(fieldName)) {
                    return Optional.ofNullable(fieldNode.getFirstChild()).map(Node::getNodeValue);
                }
            }
        }

        return Optional.empty();
    }
}
