/*
 * Copyright 2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.core.fields;

import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JacksonComponent;
import org.springframework.boot.jackson.ObjectValueDeserializer;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Custom deserializer for {@link TagName} which takes a String path and produces a normalised id and a prettified display name
 */
@JacksonComponent
class TagNameDeserializer extends ObjectValueDeserializer<TagName> {
    private final TagNameFactory tagNameFactory;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public TagNameDeserializer(final TagNameFactory tagNameFactory) {
        this.tagNameFactory = tagNameFactory;
    }

    @Override
    protected TagName deserializeObject(final JsonParser jsonParser, final DeserializationContext context, final JsonNode tree) {
        final String path = objectMapper.treeToValue(tree, String.class);
        return tagNameFactory.buildTagName(path);
    }
}
