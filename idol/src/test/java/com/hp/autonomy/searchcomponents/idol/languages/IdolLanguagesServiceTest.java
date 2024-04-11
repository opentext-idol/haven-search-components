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
import com.autonomy.aci.client.util.ActionParameters;
import com.opentext.idol.types.marshalling.ProcessorFactory;
import com.opentext.idol.types.responses.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private ProcessorFactory aciResponseProcessorFactory;

    private IdolLanguagesService idolLanguagesService;

    @Before
    public void setUp() {
        when(aciResponseProcessorFactory.getResponseDataProcessor(GetStatusResponseData.class)).thenReturn(getStatusProcessor);
        when(aciResponseProcessorFactory.getResponseDataProcessor(LanguageSettingsResponseData.class)).thenReturn(languageSettingsProcessor);
        
        idolLanguagesService = new IdolLanguagesServiceImpl(contentAciService, aciResponseProcessorFactory);
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
        languages.setDefaultLanguageType(sampleLanguage);
        languageSettingsResponseData.setLanguages(languages);

        when(contentAciService.executeAction(any(ActionParameters.class), eq(languageSettingsProcessor))).thenReturn(languageSettingsResponseData);
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
        languageTypes.add(newLanguageType("AFRIKAANS", "afrikaansAWESOME", "UTF8", 10));
        languageTypes.add(newLanguageType("CHINESE", "chineseUTF8", "UTF8", 2));
        languageTypes.add(newLanguageType("ENGLISH", "englishASCII", "ASCII", 102));
        languageTypes.add(newLanguageType("ENGLISH", "englishUTF8", "UTF8", 25));
        languageTypes.add(newLanguageType("FRENCH", "frenchASCII", "ASCII", 25));
        getStatusResponseData.setLanguageTypeSettings(languageTypeSettings);
        when(contentAciService.executeAction(any(ActionParameters.class), eq(getStatusProcessor))).thenReturn(getStatusResponseData);
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
