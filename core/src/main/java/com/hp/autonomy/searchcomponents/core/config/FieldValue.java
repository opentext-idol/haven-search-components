package com.hp.autonomy.searchcomponents.core.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * Wrapper for field value containing a display value in addition to the raw value
 */
@Data
@Jacksonized
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldValue<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 2537048632015668410L;

    private final T value;
    private final String displayValue;
}
