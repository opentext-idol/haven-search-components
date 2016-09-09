package com.hp.autonomy.searchcomponents.hod.authentication;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.hp.autonomy.hod.sso.HodUserMetadata;
import com.hp.autonomy.hod.sso.HodUserMetadataResolver;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
@Component
public class HsodUserMetadataResolver implements HodUserMetadataResolver {
    public static final String LEGACY_USER_DISPLAY_NAME = "HAVEN_SEARCH_ONDEMAND_USERNAME";
    public static final String USER_DISPLAY_NAME = "DisplayName";

    @SuppressWarnings("unused")
    public static final Map<String, Class<? extends Serializable>> METADATA_TYPES = ImmutableMap.<String, Class<? extends Serializable>>builder()
            .put(LEGACY_USER_DISPLAY_NAME, String.class)
            .put(USER_DISPLAY_NAME, String.class)
            .build();

    @Override
    public HodUserMetadata resolve(final Map<String, JsonNode> metadata) {
        return new HodUserMetadata(findCorrectUsernameFromMetadata(metadata), Collections.emptyMap());
    }

    private String findCorrectUsernameFromMetadata(final Map<String, JsonNode> metadata) {
        final String displayName;

        if (metadata.containsKey(USER_DISPLAY_NAME)) {
            displayName = parseJsonNode(metadata.get(USER_DISPLAY_NAME));

        } else if (metadata.containsKey(LEGACY_USER_DISPLAY_NAME)) {
            displayName = parseJsonNode(metadata.get(LEGACY_USER_DISPLAY_NAME));

        } else {
            displayName = null;
        }
        return displayName;
    }

    private String parseJsonNode(final JsonNode jsonNode) {
        if (jsonNode.isArray()) {
            final JsonNode firstName = jsonNode.get(0);

            return firstName != null ? firstName.asText() : null;
        } else {
            return jsonNode.asText();
        }
    }
}
