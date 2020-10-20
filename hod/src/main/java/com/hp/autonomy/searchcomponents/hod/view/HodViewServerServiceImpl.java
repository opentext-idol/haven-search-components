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

package com.hp.autonomy.searchcomponents.hod.view;

import com.hp.autonomy.aci.content.fieldtext.FieldText;
import com.hp.autonomy.aci.content.fieldtext.MATCH;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.hod.client.api.analysis.viewdocument.ViewDocumentRequestBuilder;
import com.hp.autonomy.hod.client.api.analysis.viewdocument.ViewDocumentService;
import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.hod.client.api.textindex.query.content.GetContentRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.content.GetContentService;
import com.hp.autonomy.hod.client.api.textindex.query.search.*;
import com.hp.autonomy.hod.client.error.HodErrorCode;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hp.autonomy.searchcomponents.core.view.ViewServerService;
import com.hp.autonomy.searchcomponents.core.view.raw.RawContentViewer;
import com.hp.autonomy.searchcomponents.core.view.raw.RawDocument;
import com.hp.autonomy.searchcomponents.hod.configuration.HodSearchCapable;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.hp.autonomy.searchcomponents.core.view.ViewServerService.VIEW_SERVER_SERVICE_BEAN_NAME;

/**
 * Implementation of {@link ViewServerService}, using the java HOD client.
 */
@SuppressWarnings("WeakerAccess")
@Service(VIEW_SERVER_SERVICE_BEAN_NAME)
class HodViewServerServiceImpl implements HodViewServerService {
    // Field on text index documents which (when present) contains the view URL
    private static final String URL_FIELD = "url";

    private static final String CONTENT_FIELD = "static_content";
    private static final String TITLE_FIELD = "static_title";
    private static final String REFERENCE_FIELD = "static_reference";
    private static final String HOD_RULE_CATEGORY = "default";

    private final ViewDocumentService viewDocumentService;
    private final GetContentService<Document> getContentService;
    private final QueryTextIndexService<Document> queryTextIndexService;
    private final ConfigService<? extends HodSearchCapable> configService;
    private final AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationInformationRetriever;
    private final RawContentViewer rawContentViewer;

    @Autowired
    HodViewServerServiceImpl(
            final ViewDocumentService viewDocumentService,
            final GetContentService<Document> viewGetContentService,
            final QueryTextIndexService<Document> queryTextIndexService,
            final ConfigService<? extends HodSearchCapable> configService, final AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationInformationRetriever,
            final RawContentViewer rawContentViewer
    ) {
        this.viewDocumentService = viewDocumentService;
        getContentService = viewGetContentService;
        this.queryTextIndexService = queryTextIndexService;
        this.configService = configService;
        this.authenticationInformationRetriever = authenticationInformationRetriever;
        this.rawContentViewer = rawContentViewer;
    }

    private String hodFieldValueAsString(final Object value) {
        return value instanceof List ? ((List<?>) value).get(0).toString() : value.toString();
    }

    @Override
    public void viewDocument(final HodViewRequest request, final OutputStream outputStream) throws IOException, HodErrorException {
        final String reference = request.getDocumentReference();
        final GetContentRequestBuilder getContentParams = new GetContentRequestBuilder().setPrint(Print.all);
        final QueryResults<Document> documents = getContentService.getContent(Collections.singletonList(reference), request.getDatabase(), getContentParams);

        // This document will always exist because the GetContentService.getContent throws a HodErrorException if the
        // reference doesn't exist in the index
        Document document = null;
        for (final Document document1 : documents.getDocuments()) {
            if (Objects.equals(document1.getReference(), reference)) {
                document = document1;
                break;
            }
        }

        assert document != null;
        final Map<String, Serializable> fields = document.getFields();
        final Object urlField = fields.get(URL_FIELD);

        final String documentUrl = urlField instanceof List ? ((List<?>) urlField).get(0).toString() : document.getReference();

        final UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_2_SLASHES);

        InputStream inputStream = null;
        try {
            inputStream = getInputStream(request.getHighlightExpression(), document, documentUrl, urlValidator);

            IOUtils.copy(inputStream, outputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @SuppressWarnings("resource")
    private InputStream getInputStream(final String highlightExpression,
                                       final Document document,
                                       final String documentUrl,
                                       final UrlValidator urlValidator) throws HodErrorException {
        InputStream inputStream;
        try {
            final URL url = new URL(documentUrl);
            final URI uri = new URI(url.getProtocol(), url.getAuthority(), url.getPath(), url.getQuery(), null);
            final String encodedUrl = uri.toASCIIString();

            if (urlValidator.isValid(encodedUrl)) {
                final ViewDocumentRequestBuilder builder = new ViewDocumentRequestBuilder();

                if (highlightExpression != null) {
                    builder
                            .addHighlightExpressions(highlightExpression)
                            .addEndTags(HIGHLIGHT_END_TAG)
                            .addStartTags(HIGHLIGHT_START_TAG);
                }

                inputStream = viewDocumentService.viewUrl(encodedUrl, builder);
            } else {
                inputStream = rawContentViewer.formatRawContent(documentToRawDocument(document));
            }
        } catch (final URISyntaxException | MalformedURLException ignored) {
            // URL was not valid, fall back to using the document content
            inputStream = rawContentViewer.formatRawContent(documentToRawDocument(document));
        } catch (final HodErrorException e) {
            if (e.getErrorCode() == HodErrorCode.BACKEND_REQUEST_FAILED) {
                // HOD failed to read the url, fall back to using the document content
                inputStream = rawContentViewer.formatRawContent(documentToRawDocument(document));
            } else {
                throw e;
            }
        }
        return inputStream;
    }

    @Override
    public void viewStaticContentPromotion(final String documentReference, final OutputStream outputStream) throws IOException, HodErrorException {
        final FieldText fieldText = new MATCH(REFERENCE_FIELD, documentReference)
                .AND(new MATCH("category", HOD_RULE_CATEGORY));

        final String domain = authenticationInformationRetriever.getPrincipal().getApplication().getDomain();
        final String queryManipulationIndex = configService.getConfig().getQueryManipulation().getIndex();

        final QueryRequestBuilder queryParams = new QueryRequestBuilder()
                .setFieldText(fieldText.toString())
                .setIndexes(Collections.singletonList(new ResourceName(domain, queryManipulationIndex)))
                .setPrint(Print.all);

        final QueryResults<Document> documents = queryTextIndexService.queryTextIndexWithText("*", queryParams);
        final Map<String, Serializable> fields = documents.getDocuments().get(0).getFields();

        final String staticContent = hodFieldValueAsString(fields.get(CONTENT_FIELD));
        final String staticTitle = hodFieldValueAsString(fields.get(TITLE_FIELD));

        final RawDocument rawDocument = RawDocument.builder()
                .title(staticTitle)
                .content(staticContent)
                .build();

        try (InputStream inputStream = rawContentViewer.formatRawContent(rawDocument)) {
            IOUtils.copy(inputStream, outputStream);
        }
    }

    private RawDocument documentToRawDocument(final Document document) {
        return RawDocument.builder()
                .reference(document.getReference())
                .title(document.getTitle())
                .content(document.getContent())
                .build();
    }
}
