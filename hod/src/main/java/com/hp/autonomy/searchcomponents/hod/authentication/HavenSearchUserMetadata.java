package com.hp.autonomy.searchcomponents.hod.authentication;

import com.google.common.collect.ImmutableMap;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public interface HavenSearchUserMetadata {
    String LEGACY_USER_DISPLAY_NAME = "HAVEN_SEARCH_ONDEMAND_USERNAME";
    String USER_DISPLAY_NAME = "DisplayName";

    @SuppressWarnings("unused")
    Map<String, Class<? extends Serializable>> METADATA_TYPES = ImmutableMap.<String, Class<? extends Serializable>>builder()
            .put(LEGACY_USER_DISPLAY_NAME, String.class)
            .put(USER_DISPLAY_NAME, String.class)
            .build();
}
