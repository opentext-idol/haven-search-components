/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.languages;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.core.languages.LanguagesService;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import com.hp.autonomy.types.idol.responses.GetStatusResponseData;
import com.hp.autonomy.types.idol.responses.LanguageSettingsResponseData;
import com.hp.autonomy.types.idol.responses.LanguageType;
import com.hp.autonomy.types.idol.responses.Languages;
import com.hp.autonomy.types.requests.idol.actions.general.GeneralActions;
import com.hp.autonomy.types.requests.idol.actions.status.StatusActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hp.autonomy.searchcomponents.core.languages.LanguagesService.LANGUAGES_SERVICE_BEAN_NAME;

/**
 * Default Idol implementation of {@link LanguagesService}: retrieves lists of supported languages using GetStatus and LanguageSettings actions
 */
@Service(LANGUAGES_SERVICE_BEAN_NAME)
class IdolLanguagesServiceImpl implements IdolLanguagesService {
    private static final String IDOL_UTF8_ENCODING = "UTF8";

    private final AciService contentAciService;
    private final Processor<GetStatusResponseData> getStatusProcessor;
    private final Processor<LanguageSettingsResponseData> languageSettingsProcessor;

    @Autowired
    IdolLanguagesServiceImpl(final AciService contentAciService, final ProcessorFactory processorFactory) {
        this.contentAciService = contentAciService;

        getStatusProcessor = processorFactory.getResponseDataProcessor(GetStatusResponseData.class);
        languageSettingsProcessor = processorFactory.getResponseDataProcessor(LanguageSettingsResponseData.class);
    }

    @SuppressWarnings({"ELValidationInJSP", "SpringElInspection"})
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
                languages.put(languageType.getName(), languageType);
                languagesByName.put(languageType.getLanguage(), languageType);
            } else {
                nonUtf8Languages.put(languageType.getLanguage(), languageType);
            }
        }

        nonUtf8Languages.entrySet().stream().filter(entry -> languagesByName.containsKey(entry.getKey())).forEach(entry -> {
            final LanguageType equivalentUtf8languageType = languages.get(languagesByName.get(entry.getKey()).getName());
            equivalentUtf8languageType.setDocuments(equivalentUtf8languageType.getDocuments() + entry.getValue().getDocuments());
        });

        return languages;
    }

    @SuppressWarnings({"ELValidationInJSP", "SpringElInspection"})
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
