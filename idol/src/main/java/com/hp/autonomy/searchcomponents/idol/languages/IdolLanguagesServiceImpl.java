/*
 * Copyright 2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.idol.languages;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.searchcomponents.core.languages.LanguagesService;
import com.hp.autonomy.searchcomponents.idol.annotations.IdolService;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import com.hp.autonomy.types.idol.responses.GetStatusResponseData;
import com.hp.autonomy.types.idol.responses.LanguageSettingsResponseData;
import com.hp.autonomy.types.idol.responses.LanguageType;
import com.hp.autonomy.types.idol.responses.Languages;
import com.hp.autonomy.types.requests.idol.actions.general.GeneralActions;
import com.hp.autonomy.types.requests.idol.actions.status.StatusActions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.List;
import java.util.Map;

import static com.hp.autonomy.searchcomponents.core.languages.LanguagesService.LANGUAGES_SERVICE_BEAN_NAME;

/**
 * Default Idol implementation of {@link LanguagesService}: retrieves lists of supported languages using GetStatus and LanguageSettings actions
 */
@Service(LANGUAGES_SERVICE_BEAN_NAME)
@Slf4j
@IdolService
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
        final Map<String, LanguageType> languages = new LinkedHashMap<>(languageTypes.size());
        final Map<String, LanguageType> languagesByName = new LinkedHashMap<>(languageTypes.size());
        final Map<String, LanguageType> nonUtf8Languages = new LinkedHashMap<>(languageTypes.size());
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
        try {
            final Languages languages = contentAciService.executeAction(new AciParameters(GeneralActions.LanguageSettings.name()), languageSettingsProcessor).getLanguages();
            return languages.getDefaultLanguageType();
        }
        catch(Exception e) {
            log.warn("Error while getting default languages with action=LanguageSettings, will just use first language");

            final Set<Map.Entry<String, LanguageType>> entries = getLanguages().entrySet();

            if (!entries.isEmpty()) {
                return entries.iterator().next().getValue().getName();
            }

            throw e;
        }
    }

    @Override
    @Cacheable("IdolLanguagesService.isValidLanguage")
    public boolean isValidLanguage(final String language) {
        return getLanguages().containsKey(language);
    }
}
