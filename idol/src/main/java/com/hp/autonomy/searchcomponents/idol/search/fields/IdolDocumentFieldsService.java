package com.hp.autonomy.searchcomponents.idol.search.fields;

import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService;

/**
 * Extension to {@link DocumentFieldsService} which gives explicit access to Idol hard-coded field info
 */
public interface IdolDocumentFieldsService extends DocumentFieldsService {
    /**
     * QmsId field for QMS documents
     *
     * @return QmsId field info
     */
    FieldInfo<String> getQmsIdFieldInfo();

    /**
     * InjectedPromotion field info
     *
     * @return promotion field info
     */
    FieldInfo<Boolean> getInjectedPromotionFieldInfo();
}
