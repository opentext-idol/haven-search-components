package com.hp.autonomy.searchcomponents.core.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

/**
 * State token information returned as part of response of {@link DocumentsService#getStateTokenAndResultCount(QueryRestrictions, int, boolean)}
 */
@Embeddable
@Data
@NoArgsConstructor
public class TypedStateToken implements Serializable {
    private static final long serialVersionUID = -1812490657701746949L;

    private String stateToken;

    @Enumerated(EnumType.STRING)
    private StateTokenType type;

    @JsonCreator
    public TypedStateToken(
            @JsonProperty("state_token") final String stateToken,
            @JsonProperty("type") final StateTokenType type
    ) {
        this.stateToken = stateToken;
        this.type = type;
    }

    // serialized as String - do not rename constants
    public enum StateTokenType {
        PROMOTIONS,
        QUERY
    }
}

