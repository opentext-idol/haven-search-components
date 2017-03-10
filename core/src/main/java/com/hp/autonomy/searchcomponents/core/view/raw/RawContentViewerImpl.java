package com.hp.autonomy.searchcomponents.core.view.raw;

import com.hp.autonomy.searchcomponents.core.search.DocumentTitleResolver;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component(RawContentViewer.RAW_CONTENT_VIEWER_BEAN_NAME)
class RawContentViewerImpl implements RawContentViewer {
    private static final Pattern NEWLINE_PATTERN = Pattern.compile("\n", Pattern.LITERAL);

    private String escapeAndAddLineBreaks(final String input) {
        return input == null ? "" : NEWLINE_PATTERN.matcher(StringEscapeUtils.escapeHtml(input)).replaceAll(Matcher.quoteReplacement("<br>"));
    }

    @Override
    public InputStream formatRawContent(final RawDocument rawDocument) {
        final String title = DocumentTitleResolver.resolveTitle(rawDocument.getTitle(), rawDocument.getReference());

        final String body = "<h1>" + escapeAndAddLineBreaks(title) + "</h1>"
                + "<p>" + escapeAndAddLineBreaks(rawDocument.getContent()) + "</p>";

        return IOUtils.toInputStream(body, StandardCharsets.UTF_8);
    }
}
