package com.hp.autonomy.searchcomponents.core.search;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

@SuppressWarnings("UtilityClass")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DocumentTitleResolver {
    private static final Pattern PATH_SEPARATOR_REGEX = Pattern.compile("/|\\\\");

    /**
     * Determine a title to use for a document, given it's title and reference fields.
     * @param title The document's title field
     * @param reference The document's reference field
     * @return The title to use (may be null if title and reference were null)
     */
    public static String resolveTitle(final String title, final String reference) {
        if (StringUtils.isBlank(title) && !StringUtils.isBlank(reference)) {
            // If there is no title, assume the reference is a path and take the last part (the "file name")
            final String[] splitReference = PATH_SEPARATOR_REGEX.split(reference);
            final String lastPart = splitReference[splitReference.length - 1];

            return StringUtils.isBlank(lastPart) || reference.endsWith("/") || reference.endsWith("\\") ? reference : lastPart;
        } else {
            return title;
        }
    }
}
