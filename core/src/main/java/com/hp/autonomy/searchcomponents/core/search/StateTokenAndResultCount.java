package com.hp.autonomy.searchcomponents.core.search;

import lombok.Data;

@Data
public class StateTokenAndResultCount {
    private final String stateToken;
    private final long resultCount;
}
