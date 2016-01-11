/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.languages;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.types.idol.GetStatusResponseData;
import com.hp.autonomy.types.idol.LanguageSettingsResponseData;
import com.hp.autonomy.types.idol.LanguageType;
import com.hp.autonomy.types.idol.LanguageTypeSettings;
import com.hp.autonomy.types.idol.Languages;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IdolLanguagesServiceTest {
    @Mock
    private AciService contentAciService;
    @Mock
    private Processor<GetStatusResponseData> getStatusProcessor;
    @Mock
    private Processor<LanguageSettingsResponseData> languageSettingsProcessor;
    @Mock
    private AciResponseJaxbProcessorFactory aciResponseProcessorFactory;

    private IdolLanguagesService idolLanguagesService;

    @Before
    public void setUp() {
        when(aciResponseProcessorFactory.createAciResponseProcessor(GetStatusResponseData.class)).thenReturn(getStatusProcessor);
        when(aciResponseProcessorFactory.createAciResponseProcessor(LanguageSettingsResponseData.class)).thenReturn(languageSettingsProcessor);
        
        idolLanguagesService = new IdolLanguagesService(contentAciService, aciResponseProcessorFactory);
    }

    @Test
    public void getLanguages() {
        mockGetStatusLanguageResponse();

        final Map<String, LanguageType> languages = idolLanguagesService.getLanguages();
        assertThat(languages.keySet(), hasSize(3));
        assertThat(languages, hasEntry(is("afrikaansAWESOME"), hasProperty("documents", is(10L))));
        assertThat(languages, hasEntry(is("chineseUTF8"), hasProperty("documents", is(2L))));
        assertThat(languages, hasEntry(is("englishUTF8"), hasProperty("documents", is(102L + 25L))));
        assertFalse(languages.containsKey("frenchASCII"));
    }

    @Test
    public void getDefaultLanguageId() {
        final String sampleLanguage = "samothracianUTF8";

        final LanguageSettingsResponseData languageSettingsResponseData = new LanguageSettingsResponseData();
        final Languages languages = new Languages();
        languages.setDefaultLanguage(sampleLanguage);
        languages.setDefaultEncoding(IdolLanguagesService.IDOL_UTF8_ENCODING);
        languageSettingsResponseData.setLanguages(languages);

        when(contentAciService.executeAction(any(AciParameters.class), eq(languageSettingsProcessor))).thenReturn(languageSettingsResponseData);
        assertEquals(sampleLanguage, idolLanguagesService.getDefaultLanguageId());
    }

    @Test
    public void isValidLanguage() {
        mockGetStatusLanguageResponse();

        assertTrue(idolLanguagesService.isValidLanguage("afrikaansAWESOME"));
        assertFalse(idolLanguagesService.isValidLanguage("frenchASCII"));
    }

    private void mockGetStatusLanguageResponse() {
        final GetStatusResponseData getStatusResponseData = new GetStatusResponseData();
        final LanguageTypeSettings languageTypeSettings = new LanguageTypeSettings();
        final List<LanguageType> languageTypes = languageTypeSettings.getLanguageType();
        languageTypes.add(newLanguageType("afrikaansAWESOME", "AFRIKAANS", "UTF8", 10));
        languageTypes.add(newLanguageType("chineseUTF8", "CHINESE", "UTF8", 2));
        languageTypes.add(newLanguageType("englishASCII", "ENGLISH", "ASCII", 102));
        languageTypes.add(newLanguageType("englishUTF8", "ENGLISH", "UTF8", 25));
        languageTypes.add(newLanguageType("frenchASCII", "FRENCH", "ASCII", 25));
        getStatusResponseData.setLanguageTypeSettings(languageTypeSettings);
        when(contentAciService.executeAction(any(AciParameters.class), eq(getStatusProcessor))).thenReturn(getStatusResponseData);
    }

    private LanguageType newLanguageType(final String language, final String name, final String encoding, final int numberOfDocuments) {
        final LanguageType languageType = new LanguageType();
        languageType.setLanguage(language);
        languageType.setName(name);
        languageType.setEncoding(encoding);
        languageType.setDocuments(numberOfDocuments);
        return languageType;
    }
}
