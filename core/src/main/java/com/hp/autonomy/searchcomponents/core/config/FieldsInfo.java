/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("InstanceVariableOfConcreteClass")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldsInfo implements Serializable {
    private static final long serialVersionUID = 7627012722603736269L;

    private FieldAssociations fieldAssociations;
    private Set<FieldInfo<?>> customFields;

    public FieldsInfo merge(final FieldsInfo other) {
        if (other != null) {
            fieldAssociations = fieldAssociations == null ? other.fieldAssociations : fieldAssociations.merge(other.fieldAssociations);
            if (customFields == null) {
                customFields = other.customFields;
            } else {
                final Set<FieldInfo<?>> mergedCustomFields = new HashSet<>(other.customFields);
                mergedCustomFields.addAll(customFields);
                customFields = mergedCustomFields;
            }
        }

        return this;
    }
}
