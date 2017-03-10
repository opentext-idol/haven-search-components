package com.hp.autonomy.searchcomponents.core.fields;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * Custom deserialization of {@link FieldInfo}
 */
@SuppressWarnings("unused")
@JsonComponent
public class FieldPathSerializer extends JsonSerializer<FieldPath> {
    @Override
    public void serialize(final FieldPath value, final JsonGenerator jsonGenerator, final SerializerProvider serializers) throws IOException {
        jsonGenerator.writeString(value.getNormalisedPath());
    }
}
