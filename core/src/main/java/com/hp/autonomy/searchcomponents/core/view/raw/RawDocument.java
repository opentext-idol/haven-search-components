package com.hp.autonomy.searchcomponents.core.view.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class RawDocument {
    private final String reference;
    private final String title;
    private final String content;
}
