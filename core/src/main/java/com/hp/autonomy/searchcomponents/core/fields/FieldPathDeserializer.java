package com.hp.autonomy.searchcomponents.core.fields;

import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JacksonComponent;
import org.springframework.boot.jackson.ObjectValueDeserializer;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Custom deserialization of {@link FieldInfo}
 */
@SuppressWarnings("unused")
@JacksonComponent
public class FieldPathDeserializer extends ObjectValueDeserializer<FieldPath> {
    private final FieldPathNormaliser fieldPathNormaliser;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public FieldPathDeserializer(final FieldPathNormaliser fieldPathNormaliser) {
        this.fieldPathNormaliser = fieldPathNormaliser;
    }

    @Override
    protected FieldPath deserializeObject(final JsonParser jsonParser, final DeserializationContext context, final JsonNode jsonNode) {
        final String path = objectMapper.treeToValue(jsonNode, String.class);
        return fieldPathNormaliser.normaliseFieldPath(path);
    }
}
