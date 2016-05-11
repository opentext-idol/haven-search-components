/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.fields;

import com.hp.autonomy.types.requests.idol.actions.tags.TagResponse;
import lombok.Data;
import lombok.experimental.Builder;

import java.util.List;

@Data
@Builder(fluent = false)
public class IdolTagResponse implements TagResponse {
    private final List<String> indexTypeFields;
    private final List<String> parametricTypeFields;
    private final List<String> numericTypeFields;
    private final List<String> autnRankTypeFields;
    private final List<String> referenceTypeFields;
    private final List<String> dateTypeFields;
    private final List<String> storedTypeFields;
}
