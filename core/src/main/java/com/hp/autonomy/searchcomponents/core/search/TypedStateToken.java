package com.hp.autonomy.searchcomponents.core.search;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * State token information returned as part of response of
 * {@link DocumentsService#getStateTokenAndResultCount}
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypedStateToken implements Serializable {
    private static final long serialVersionUID = -1812490657701746949L;

    private String stateToken;

    @Enumerated(EnumType.STRING)
    private StateTokenType type;

    // serialized as String - do not rename constants
    public enum StateTokenType {
        PROMOTIONS,
        QUERY
    }
}

