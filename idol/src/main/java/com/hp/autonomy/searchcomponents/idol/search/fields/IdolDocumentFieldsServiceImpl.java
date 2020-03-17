/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search.fields;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.config.FieldType;
import com.hp.autonomy.searchcomponents.core.config.HavenSearchCapable;
import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import com.hp.autonomy.searchcomponents.core.search.fields.AbstractDocumentFieldsService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

import static com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService.DOCUMENT_FIELDS_SERVICE_BEAN_NAME;

@Getter
@SuppressWarnings("InstanceVariableOfConcreteClass")
@Component(DOCUMENT_FIELDS_SERVICE_BEAN_NAME)
class IdolDocumentFieldsServiceImpl extends AbstractDocumentFieldsService implements IdolDocumentFieldsService {
    static final String QMS_ID_FIELD = "QMSID";
    static final String INJECTED_PROMOTION_FIELD = "INJECTEDPROMOTION";
    static final String FACT_ID_FIELD = "FACTS/FACT_EXTRACT_/ID";
    static final String FACT_SENTENCE_FIELD = "FACTS/FACT_EXTRACT_/SENTENCE";
    private final FieldInfo<String> qmsIdFieldInfo;
    private final FieldInfo<Boolean> injectedPromotionFieldInfo;
    private final FieldInfo<String> factIdFieldInfo;
    private final FieldInfo<String> factSentenceFieldInfo;

    @SuppressWarnings("TypeMayBeWeakened")
    @Autowired
    IdolDocumentFieldsServiceImpl(final ConfigService<? extends HavenSearchCapable> configService,
                                  final FieldPathNormaliser fieldPathNormaliser) {
        super(configService);

        qmsIdFieldInfo = FieldInfo.<String>builder()
                .name(fieldPathNormaliser.normaliseFieldPath(QMS_ID_FIELD))
                .advanced(true)
                .build();

        injectedPromotionFieldInfo = FieldInfo.<Boolean>builder()
                .name(fieldPathNormaliser.normaliseFieldPath(INJECTED_PROMOTION_FIELD))
                .type(FieldType.BOOLEAN)
                .advanced(true)
                .build();

        factIdFieldInfo = FieldInfo.<String>builder()
            .id(FACT_ID_FIELD)
            .name(fieldPathNormaliser.normaliseFieldPath(FACT_ID_FIELD))
            .advanced(true)
            .build();

        factSentenceFieldInfo = FieldInfo.<String>builder()
            .id(FACT_SENTENCE_FIELD)
            .name(fieldPathNormaliser.normaliseFieldPath(FACT_SENTENCE_FIELD))
            .advanced(true)
            .build();
    }

    @Override
    public Collection<FieldInfo<?>> getHardCodedFields() {
        return Arrays.asList(
            qmsIdFieldInfo, injectedPromotionFieldInfo, factIdFieldInfo, factSentenceFieldInfo);
    }
}
