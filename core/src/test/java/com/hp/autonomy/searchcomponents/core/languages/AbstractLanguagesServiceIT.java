/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.languages;

import com.hp.autonomy.types.idol.LanguageType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public abstract class AbstractLanguagesServiceIT {
    @Autowired
    private LanguagesService languagesService;

    @Test
    public void getLanguages() {
        final Map<String, LanguageType> languages = languagesService.getLanguages();
        assertThat(languages.keySet(), is(not(empty())));
    }

    @Test
    public void getDefaultLanguageId() {
        assertNotNull(languagesService.getDefaultLanguageId());
    }

    @Test
    public void isValidLanguage() {
        assertTrue(languagesService.isValidLanguage(languagesService.getDefaultLanguageId()));
    }
}
