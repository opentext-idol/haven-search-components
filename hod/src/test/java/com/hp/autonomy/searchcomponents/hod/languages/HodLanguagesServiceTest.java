/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.hod.languages;

import com.hp.autonomy.searchcomponents.core.languages.LanguagesService;
import org.junit.Before;
import org.junit.Test;

import static com.hp.autonomy.searchcomponents.hod.languages.HodLanguagesService.THE_LANGUAGE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HodLanguagesServiceTest {
    private LanguagesService languagesService;

    @Before
    public void setUp() {
        languagesService = new HodLanguagesServiceImpl();
    }

    @Test
    public void getLanguages() {
        assertThat(languagesService.getLanguages().keySet(), hasSize(1));
    }

    @Test
    public void getDefaultLanguageId() {
        assertEquals(THE_LANGUAGE, languagesService.getDefaultLanguageId());
    }

    @Test
    public void validLanguage() {
        assertTrue(languagesService.isValidLanguage(THE_LANGUAGE));
    }

    @Test
    public void invalidLanguage() {
        assertFalse(languagesService.isValidLanguage("bad"));
    }
}
