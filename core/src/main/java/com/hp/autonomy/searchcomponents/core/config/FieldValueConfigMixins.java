package com.hp.autonomy.searchcomponents.core.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Configures JSON type information for {@link FieldValue} when reading the config file.
 *
 * @param <T> The value type
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface FieldValueConfigMixins<T> {

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, defaultImpl = String.class)
    T getValue();

}
