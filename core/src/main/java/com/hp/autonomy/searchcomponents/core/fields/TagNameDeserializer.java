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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.boot.jackson.JsonObjectDeserializer;

import java.io.IOException;

/**
 * Custom deserializer for {@link TagName} which takes a String path and produces a normalised id and a prettified display name
 */
@JsonComponent
class TagNameDeserializer extends JsonObjectDeserializer<TagName> {
    private final TagNameFactory tagNameFactory;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public TagNameDeserializer(final TagNameFactory tagNameFactory) {
        this.tagNameFactory = tagNameFactory;
    }

    @Override
    protected TagName deserializeObject(final JsonParser jsonParser, final DeserializationContext context, final ObjectCodec codec, final JsonNode tree) throws IOException {
        final String path = objectMapper.treeToValue(tree, String.class);
        return tagNameFactory.buildTagName(path);
    }
}
