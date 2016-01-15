/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.languages;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.searchcomponents.core.languages.LanguagesService;
import com.hp.autonomy.types.idol.GetStatusResponseData;
import com.hp.autonomy.types.idol.LanguageSettingsResponseData;
import com.hp.autonomy.types.idol.LanguageType;
import com.hp.autonomy.types.idol.Languages;
import com.hp.autonomy.types.requests.idol.actions.general.GeneralActions;
import com.hp.autonomy.types.requests.idol.actions.status.StatusActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IdolLanguagesService implements LanguagesService {
    static final String IDOL_UTF8_ENCODING = "UTF8";

    private final AciService contentAciService;
    private final Processor<GetStatusResponseData> getStatusProcessor;
    private final Processor<LanguageSettingsResponseData> languageSettingsProcessor;

    @Autowired
    public IdolLanguagesService(final AciService contentAciService, final AciResponseJaxbProcessorFactory aciResponseProcessorFactory) {
        this.contentAciService = contentAciService;

        getStatusProcessor = aciResponseProcessorFactory.createAciResponseProcessor(GetStatusResponseData.class);
        languageSettingsProcessor = aciResponseProcessorFactory.createAciResponseProcessor(LanguageSettingsResponseData.class);
    }

    @Override
    @Cacheable(value = "IdolLanguagesService.getLanguages", key = "#root.methodName")
    public Map<String, LanguageType> getLanguages() {
        final GetStatusResponseData getStatusResponseData = contentAciService.executeAction(new AciParameters(StatusActions.GetStatus.name()), getStatusProcessor);

        final List<LanguageType> languageTypes = getStatusResponseData.getLanguageTypeSettings().getLanguageType();
        final Map<String, LanguageType> languages = new HashMap<>(languageTypes.size());
        final Map<String, LanguageType> languagesByName = new HashMap<>(languageTypes.size());
        final Map<String, LanguageType> nonUtf8Languages = new HashMap<>(languageTypes.size());
        for (final LanguageType languageType : languageTypes) {
            if (IDOL_UTF8_ENCODING.equals(languageType.getEncoding())) {
                languages.put(languageType.getLanguage(), languageType);
                languagesByName.put(languageType.getName(), languageType);
            } else {
                nonUtf8Languages.put(languageType.getName(), languageType);
            }
        }

        for (final Map.Entry<String, LanguageType> entry : nonUtf8Languages.entrySet()) {
            if (languagesByName.containsKey(entry.getKey())) {
                final LanguageType equivalentUtf8languageType = languages.get(languagesByName.get(entry.getKey()).getLanguage());
                equivalentUtf8languageType.setDocuments(equivalentUtf8languageType.getDocuments() + entry.getValue().getDocuments());
            }
        }

        return languages;
    }

    @Override
    @Cacheable(value = "IdolLanguagesService.getDefaultLanguageId", key = "#root.methodName")
    public String getDefaultLanguageId() {
        final Languages languages = contentAciService.executeAction(new AciParameters(GeneralActions.LanguageSettings.name()), languageSettingsProcessor).getLanguages();
        return languages.getDefaultLanguageType();
    }

    @Override
    @Cacheable("IdolLanguagesService.isValidLanguage")
    public boolean isValidLanguage(final String language) {
        return getLanguages().containsKey(language);
    }
}
