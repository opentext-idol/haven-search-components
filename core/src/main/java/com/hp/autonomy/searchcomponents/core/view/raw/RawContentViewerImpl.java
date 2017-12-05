package com.hp.autonomy.searchcomponents.core.view.raw;

import com.hp.autonomy.searchcomponents.core.search.DocumentTitleResolver;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hp.autonomy.searchcomponents.core.view.ViewServerService.HIGHLIGHT_END_TAG;
import static com.hp.autonomy.searchcomponents.core.view.ViewServerService.HIGHLIGHT_START_TAG;

@Component(RawContentViewer.RAW_CONTENT_VIEWER_BEAN_NAME)
class RawContentViewerImpl implements RawContentViewer {
    private static final Pattern NEWLINE_PATTERN = Pattern.compile("\n", Pattern.LITERAL);
    private static final Pattern HIGHLIGHT_PATTERN = Pattern.compile(Pattern.quote(HIGHLIGHT_START_TAG) + "(.*?)" + Pattern.quote(HIGHLIGHT_END_TAG));

    private String escapeHtmlExceptHighlighting(final String input) {
        final Matcher matcher = HIGHLIGHT_PATTERN.matcher(input);

        int prev = 0;
        final StringBuilder sb = new StringBuilder();

        while(matcher.find()) {
            final int start = matcher.start();
            if (prev < start) {
                sb.append(StringEscapeUtils.escapeHtml(input.substring(prev, start)));
            }

            prev = matcher.end();

            sb.append(HIGHLIGHT_START_TAG)
              .append(StringEscapeUtils.escapeHtml(matcher.group(1)))
              .append(HIGHLIGHT_END_TAG);
        }

        if (prev < input.length()) {
            sb.append(StringEscapeUtils.escapeHtml(input.substring(prev)));
        }

        return sb.toString();
    }

    private String escapeAndAddLineBreaks(final String input) {
        return input == null ? "" : NEWLINE_PATTERN.matcher(escapeHtmlExceptHighlighting(input)).replaceAll(Matcher.quoteReplacement("<br>"));
    }

    @Override
    public InputStream formatRawContent(final RawDocument rawDocument) {
        final String title = DocumentTitleResolver.resolveTitle(rawDocument.getTitle(), rawDocument.getReference());

        final String body = "<h1>" + escapeAndAddLineBreaks(title) + "</h1>"
                + "<p>" + escapeAndAddLineBreaks(rawDocument.getContent()) + "</p>";

        return IOUtils.toInputStream(body, StandardCharsets.UTF_8);
    }
}
