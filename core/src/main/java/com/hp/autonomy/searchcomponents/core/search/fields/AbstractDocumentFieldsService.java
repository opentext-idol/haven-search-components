/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search.fields;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.core.config.HavenSearchCapable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractDocumentFieldsService implements DocumentFieldsService {
    private final ConfigService<? extends HavenSearchCapable> configService;

    protected AbstractDocumentFieldsService(final ConfigService<? extends HavenSearchCapable> configService) {
        this.configService = configService;
    }

    @Override
    public List<String> getPrintFields(final Collection<String> selectedFields) {
        final FieldsInfo fieldsInfo = configService.getConfig().getFieldsInfo();

        final List<String> fields = new ArrayList<>();

        getHardCodedFields().stream()
                .filter(field -> selectedFields.isEmpty() || selectedFields.contains(field.getId()))
                .forEach(field -> fields.addAll(field.getNames()));

        final Collection<FieldInfo<?>> fieldConfig = fieldsInfo.getFieldConfig().values();

        fieldConfig.stream()
                .filter(field -> selectedFields.isEmpty() || selectedFields.contains(field.getId()))
                .forEach(field -> fields.addAll(field.getNames()));

        return fields;
    }
}
