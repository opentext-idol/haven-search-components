package com.hp.autonomy.searchcomponents.core.fields;

import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import org.springframework.boot.jackson.JacksonComponent;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * Custom deserialization of {@link FieldInfo}
 */
@SuppressWarnings("unused")
@JacksonComponent
public class FieldPathSerializer extends ValueSerializer<FieldPath> {

    @Override
    public void serialize(final FieldPath value, final JsonGenerator jsonGenerator, final SerializationContext context) {
        jsonGenerator.writeString(value.getNormalisedPath());
    }

}
