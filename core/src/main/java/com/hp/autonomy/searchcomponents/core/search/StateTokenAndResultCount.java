package com.hp.autonomy.searchcomponents.core.search;

import lombok.Data;

import java.io.Serializable;

/**
 * Response returned by {@link DocumentsService#getStateTokenAndResultCount}
 */
@Data
public class StateTokenAndResultCount implements Serializable {
    private static final long serialVersionUID = 409875962484297815L;

    @SuppressWarnings("InstanceVariableOfConcreteClass")
    private final TypedStateToken typedStateToken;
    private final long resultCount;
}
