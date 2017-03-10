/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.fields;

import com.hp.autonomy.searchcomponents.core.config.FieldType;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.hp.autonomy.searchcomponents.core.fields.TagNameFactory.TAG_NAME_FACTORY_BEAN_NAME;

/**
 * Default implementation of {@link TagNameFactory}
 */
@Component(TAG_NAME_FACTORY_BEAN_NAME)
public class TagNameFactoryImpl implements TagNameFactory {
    private final FieldPathNormaliser fieldPathNormaliser;
    private final FieldDisplayNameGenerator fieldDisplayNameGenerator;

    @Autowired
    public TagNameFactoryImpl(final FieldPathNormaliser fieldPathNormaliser,
                              final FieldDisplayNameGenerator fieldDisplayNameGenerator) {
        this.fieldPathNormaliser = fieldPathNormaliser;
        this.fieldDisplayNameGenerator = fieldDisplayNameGenerator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TagName buildTagName(final String path) {
        final FieldPath fieldPath = getFieldPath(path);
        final String displayName = fieldDisplayNameGenerator.generateDisplayName(fieldPath);

        return new TagNameImpl(fieldPath, displayName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldPath getFieldPath(final String path) {
        return fieldPathNormaliser.normaliseFieldPath(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTagDisplayValue(final String path, final String value) {
        final FieldPath fieldPath = getFieldPath(path);
        return fieldDisplayNameGenerator.generateDisplayValue(fieldPath, value, FieldType.STRING);
    }
}
