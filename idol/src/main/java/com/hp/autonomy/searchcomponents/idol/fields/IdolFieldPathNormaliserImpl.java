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

package com.hp.autonomy.searchcomponents.idol.fields;

import com.hp.autonomy.searchcomponents.core.fields.AbstractFieldPathNormaliser;
import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import static com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser.FIELD_PATH_NORMALISER_BEAN_NAME;

/**
 * Default Idol implementation of {@link FieldPathNormaliser}
 */
@Component(FIELD_PATH_NORMALISER_BEAN_NAME)
public class IdolFieldPathNormaliserImpl extends AbstractFieldPathNormaliser {
    private static final String IDX_PREFIX = "DOCUMENT/";
    private static final Pattern FIELD_NAME_PATTERN = Pattern.compile("(/?[^/]+)+");
    private static final Pattern IDX_PATH_PATTERN = Pattern.compile("^/?(?:" + IDX_PREFIX + ")?(?<fieldPath>[^/]+)$");
    private Pattern XML_PATH_PATTERN = updatePattern(Collections.singletonList("DOCUMENTS"));

    // We have to do it this way to break the circular dependency between the FieldsInfo deserializer and this.
    public Pattern updatePattern(final Collection<String> prefixes) {
        final String XML_PREFIX = prefixes.stream()
                .map(s -> Pattern.quote(s + "/"))
                .collect(Collectors.joining("|"));

        return XML_PATH_PATTERN = Pattern.compile("^/?(?:" + XML_PREFIX + ")?(?:" + IDX_PREFIX + ")?(?<fieldPath>[^/]+(?:/[^/]+)*)$");
    }

    @Override
    public FieldPath normaliseFieldPath(final String fieldPath) {
        if (StringUtils.isBlank(fieldPath) || !FIELD_NAME_PATTERN.matcher(fieldPath.trim()).matches()) {
            throw new IllegalArgumentException("Field names may not be blank or contain only forward slashes");
        }

        String normalisedFieldName = fieldPath.toUpperCase();
        if (!ParametricValuesService.IDOL_METADATA_FIELDS.contains(normalisedFieldName)) {
            final Matcher idxMatcher = IDX_PATH_PATTERN.matcher(normalisedFieldName);
            if (idxMatcher.find()) {
                normalisedFieldName = idxMatcher.group("fieldPath");
            } else {
                final Matcher xmlMatcher = XML_PATH_PATTERN.matcher(normalisedFieldName);
                if (xmlMatcher.find()) {
                    normalisedFieldName = xmlMatcher.group("fieldPath");
                }
            }
        }

        final String fieldName = normalisedFieldName.contains("/") ? normalisedFieldName.substring(normalisedFieldName.lastIndexOf('/') + 1) : normalisedFieldName;
        return newFieldPath(normalisedFieldName, fieldName);
    }
}
