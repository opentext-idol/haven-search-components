/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.fields;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@SuppressWarnings("FieldMayBeFinal")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(builder = IdolFieldsRequest.Builder.class)
public class IdolFieldsRequest implements FieldsRequest {
    private static final long serialVersionUID = 3450911770365743948L;

    private Integer maxValues;

    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Builder {
        private Integer maxValues;

        public Builder(final FieldsRequest fieldsRequest) {
            maxValues = fieldsRequest.getMaxValues();
        }

        public IdolFieldsRequest build() {
            return new IdolFieldsRequest(maxValues);
        }
    }
}
