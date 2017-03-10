package com.hp.autonomy.searchcomponents.core.parametricvalues;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class DependentParametricField implements Serializable {
    private static final long serialVersionUID = -4957971709026141276L;

    private final String value;
    private final String displayValue;
    private final int count;
    private final List<DependentParametricField> subFields;
}
