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
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(builder = IdolFieldsRequest.Builder.class)
public class IdolFieldsRequest implements FieldsRequest {
    private static final long serialVersionUID = 3450911770365743948L;

    private Integer maxValues;

    @Component
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Builder implements FieldsRequest.Builder<IdolFieldsRequest> {
        private Integer maxValues;

        public Builder(final FieldsRequest fieldsRequest) {
            maxValues = fieldsRequest.getMaxValues();
        }

        @Override
        public IdolFieldsRequest build() {
            return new IdolFieldsRequest(maxValues);
        }
    }
}
