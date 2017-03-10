package com.hp.autonomy.searchcomponents.core.fields;

import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@EqualsAndHashCode(of = "normalisedPath")
@ToString
@RequiredArgsConstructor
class FieldPathImpl implements FieldPath {
    private static final long serialVersionUID = -2329556516045568311L;

    private final String normalisedPath;
    private final String fieldName;
}
