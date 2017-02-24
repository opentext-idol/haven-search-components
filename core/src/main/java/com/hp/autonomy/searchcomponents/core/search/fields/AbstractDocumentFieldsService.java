/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search.fields;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldsInfo;
import com.hp.autonomy.searchcomponents.core.config.HavenSearchCapable;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Skeletal implementation of {@link DocumentFieldsService} containing a default implementation of {@link DocumentFieldsService#getPrintFields(Collection)}
 */
public abstract class AbstractDocumentFieldsService implements DocumentFieldsService {
    private final ConfigService<? extends HavenSearchCapable> configService;

    protected AbstractDocumentFieldsService(final ConfigService<? extends HavenSearchCapable> configService) {
        this.configService = configService;
    }

    @Override
    public List<String> getPrintFields(final Collection<String> selectedFields) {
        final FieldsInfo fieldsInfo = configService.getConfig().getFieldsInfo();

        final Collection<FieldInfo<?>> fieldConfig = fieldsInfo.getFieldConfig().values();
        return Stream.concat(getHardCodedFields().stream(), fieldConfig.stream())
                .filter(field -> selectedFields.isEmpty() || selectedFields.contains(field.getId()))
                .flatMap(field -> field.getNames().stream())
                .map(FieldPath::getNormalisedPath)
                .collect(Collectors.toList());
    }
}
