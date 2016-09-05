/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.view;

import com.hp.autonomy.aci.content.fieldtext.FieldText;
import com.hp.autonomy.aci.content.fieldtext.MATCH;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.hod.client.api.analysis.viewdocument.ViewDocumentRequestBuilder;
import com.hp.autonomy.hod.client.api.analysis.viewdocument.ViewDocumentService;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.content.GetContentRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.content.GetContentService;
import com.hp.autonomy.hod.client.api.textindex.query.search.Document;
import com.hp.autonomy.hod.client.api.textindex.query.search.Print;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryResults;
import com.hp.autonomy.hod.client.api.textindex.query.search.QueryTextIndexService;
import com.hp.autonomy.hod.client.error.HodErrorCode;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import com.hp.autonomy.searchcomponents.core.view.ViewServerService;
import com.hp.autonomy.searchcomponents.hod.configuration.HodSearchCapable;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
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
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Implementation of {@link HodViewServerService}, using the java HOD client.
 */
@SuppressWarnings("WeakerAccess")
@Service
public class HodViewServerService implements ViewServerService<ResourceIdentifier, HodErrorException> {
    // Field on text index documents which (when present) contains the view URL
    private static final String URL_FIELD = "url";

    public static final String CONTENT_FIELD = "static_content";
    public static final String TITLE_FIELD = "static_title";
    public static final String REFERENCE_FIELD = "static_reference";
    public static final String HOD_RULE_CATEGORY = "default";

    private final ViewDocumentService viewDocumentService;
    private final GetContentService<Document> getContentService;
    private final QueryTextIndexService<Document> queryTextIndexService;
    private final ConfigService<? extends HodSearchCapable> configService;
    private final AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationInformationRetriever;

    @Autowired
    public HodViewServerService(
            final ViewDocumentService viewDocumentService,
            final GetContentService<Document> viewGetContentService,
            final QueryTextIndexService<Document> queryTextIndexService,
            final ConfigService<? extends HodSearchCapable> configService, final AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationInformationRetriever) {
        this.viewDocumentService = viewDocumentService;
        getContentService = viewGetContentService;
        this.queryTextIndexService = queryTextIndexService;
        this.configService = configService;
        this.authenticationInformationRetriever = authenticationInformationRetriever;
    }

    private String resolveTitle(final Document document) {
        if (StringUtils.isBlank(document.getTitle())) {
            // If there is no title field, assume the reference is a path and take the last part (the "file name")
            final String[] splitReference = document.getReference().split("/|\\\\");
            final String lastPart = splitReference[splitReference.length - 1];

            return StringUtils.isBlank(lastPart) ? document.getReference() : lastPart;
        } else {
            return document.getTitle();
        }
    }

    private String escapeAndAddLineBreaks(final String input) {
        return input == null ? "" : StringEscapeUtils.escapeHtml(input).replace("\n", "<br>");
    }

    // Format the document's content for display in a browser
    private InputStream formatRawContent(final Document document) {
        final String body = "<h1>" + escapeAndAddLineBreaks(resolveTitle(document)) + "</h1>"
                + "<p>" + escapeAndAddLineBreaks(document.getContent()) + "</p>";

        return IOUtils.toInputStream(body, StandardCharsets.UTF_8);
    }

    // TODO: Reconcile with the above
    private String formatRawContent(final String title, final String content) {
        return "<h1>" + escapeAndAddLineBreaks(title) + "</h1>"
                + "<p>" + escapeAndAddLineBreaks(content) + "</p>";
    }

    private String hodFieldValueAsString(final Object value) {
        return value instanceof List ? ((List<?>) value).get(0).toString() : value.toString();
    }

    @Override
    public void viewDocument(final String reference, final ResourceIdentifier index, final String highlightExpression, final OutputStream outputStream) throws IOException, HodErrorException {
        final GetContentRequestBuilder getContentParams = new GetContentRequestBuilder().setPrint(Print.all);
        final QueryResults<Document> documents = getContentService.getContent(Collections.singletonList(reference), index, getContentParams);

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
                    throw new URISyntaxException(encodedUrl, "Invalid URL");
                }
            } catch (URISyntaxException | MalformedURLException e) {
                // URL was not valid, fall back to using the document content
                inputStream = formatRawContent(document);
            } catch (final HodErrorException e) {
                if (e.getErrorCode() == HodErrorCode.BACKEND_REQUEST_FAILED) {
                    // HOD failed to read the url, fall back to using the document content
                    inputStream = formatRawContent(document);
                } else {
                    throw e;
                }
            }

            IOUtils.copy(inputStream, outputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Override
    public void viewStaticContentPromotion(final String documentReference, final OutputStream outputStream) throws IOException, HodErrorException {
        final FieldText fieldText = new MATCH(REFERENCE_FIELD, documentReference)
                .AND(new MATCH("category", HOD_RULE_CATEGORY));

        final String domain = authenticationInformationRetriever.getPrincipal().getApplication().getDomain();
        final String queryManipulationIndex = configService.getConfig().getQueryManipulation().getIndex();

        final QueryRequestBuilder queryParams = new QueryRequestBuilder()
                .setFieldText(fieldText.toString())
                .setIndexes(Collections.singletonList(new ResourceIdentifier(domain, queryManipulationIndex)))
                .setPrint(Print.all);

        final QueryResults<Document> documents = queryTextIndexService.queryTextIndexWithText("*", queryParams);
        final Map<String, Serializable> fields = documents.getDocuments().get(0).getFields();

        final String staticContent = hodFieldValueAsString(fields.get(CONTENT_FIELD));
        final String staticTitle = hodFieldValueAsString(fields.get(TITLE_FIELD));

        try (InputStream inputStream = IOUtils.toInputStream(formatRawContent(staticTitle, staticContent), StandardCharsets.UTF_8)) {
            IOUtils.copy(inputStream, outputStream);
        }
    }
}
