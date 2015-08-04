package com.hp.autonomy.frontend.view.hod;

import com.hp.autonomy.hod.client.api.analysis.viewdocument.ViewDocumentRequestBuilder;
import com.hp.autonomy.hod.client.api.analysis.viewdocument.ViewDocumentService;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.content.GetContentRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.content.GetContentService;
import com.hp.autonomy.hod.client.api.textindex.query.search.Document;
import com.hp.autonomy.hod.client.api.textindex.query.search.Documents;
import com.hp.autonomy.hod.client.api.textindex.query.search.Print;
import com.hp.autonomy.hod.client.error.HodErrorCode;
import com.hp.autonomy.hod.client.error.HodErrorException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link HodViewService}, using the java HOD client.
 */
public class HodViewServiceImpl implements HodViewService {
    // Field on text index documents which (when present) contains the view URL
    private static final String URL_FIELD = "url";

    private final ViewDocumentService viewDocumentService;
    private final GetContentService<Documents> getContentService;

    public HodViewServiceImpl(final ViewDocumentService viewDocumentService, final GetContentService<Documents> getContentService) {
        this.viewDocumentService = viewDocumentService;
        this.getContentService = getContentService;
    }

    private String resolveTitle(final Document document) {
        if (StringUtils.isBlank(document.getTitle())) {
            // If there is no title field, assume the reference is a path and take the last part (the "file name")
            final String[] splitReference = document.getReference().split("/|\\\\");
            final String lastPart = splitReference[splitReference.length - 1];

            if (StringUtils.isBlank(lastPart)) {
                // If the reference ends with a trailing slash followed by whitespace, use the whole reference
                return document.getReference();
            } else {
                return lastPart;
            }
        } else {
            return document.getTitle();
        }
    }

    private String escapeAndAddLineBreaks(final String input) {
        if (input == null) {
            return "";
        } else {
            return StringEscapeUtils.escapeHtml(input).replace("\n", "<br>");
        }
    }

    // Format the document's content for display in a browser
    private InputStream formatRawContent(final Document document) throws IOException {
        final String body = "<h1>" + escapeAndAddLineBreaks(resolveTitle(document)) + "</h1>"
                + "<p>" + escapeAndAddLineBreaks(document.getContent()) + "</p>";

        return IOUtils.toInputStream(body, StandardCharsets.UTF_8);
    }

    @Override
    public void viewDocument(final String reference, final ResourceIdentifier index, final OutputStream outputStream) throws IOException, HodErrorException {
        final GetContentRequestBuilder getContentParams = new GetContentRequestBuilder().setPrint(Print.all);
        final Documents documents = getContentService.getContent(Collections.singletonList(reference), index, getContentParams);

        // This document will always exist because the GetContentService.getContent throws a HodErrorException if the
        // reference doesn't exist in the index
        final Document document = documents.getDocuments().get(0);

        final Map<String, Object> fields = document.getFields();
        final Object urlField = fields.get(URL_FIELD);

        final String documentUrl;

        if (urlField instanceof List) {
            documentUrl = ((List<?>) urlField).get(0).toString();
        } else {
            documentUrl = document.getReference();
        }

        final UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_2_SLASHES);
        InputStream inputStream = null;

        try {
            try {
                final URL url = new URL(documentUrl);
                final URI uri = new URI(url.getProtocol(), url.getAuthority(), url.getPath(), url.getQuery(), null);
                final String encodedUrl = uri.toASCIIString();

                if (urlValidator.isValid(encodedUrl)) {
                    inputStream = viewDocumentService.viewUrl(encodedUrl, new ViewDocumentRequestBuilder());
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
}
