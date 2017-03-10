package com.hp.autonomy.searchcomponents.core.fields;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.boot.jackson.JsonObjectDeserializer;

import java.io.IOException;

/**
 * Custom deserialization of {@link FieldInfo}
 */
@SuppressWarnings("unused")
@JsonComponent
public class FieldPathDeserializer extends JsonObjectDeserializer<FieldPath> {
    private final FieldPathNormaliser fieldPathNormaliser;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public FieldPathDeserializer(final FieldPathNormaliser fieldPathNormaliser) {
        this.fieldPathNormaliser = fieldPathNormaliser;
    }

    @Override
    protected FieldPath deserializeObject(final JsonParser jsonParser, final DeserializationContext context, final ObjectCodec codec, final JsonNode jsonNode) throws IOException {
        final String path = objectMapper.treeToValue(jsonNode, String.class);
        return fieldPathNormaliser.normaliseFieldPath(path);
    }
}
