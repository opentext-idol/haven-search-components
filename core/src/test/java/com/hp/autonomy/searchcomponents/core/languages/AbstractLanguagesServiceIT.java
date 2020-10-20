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

package com.hp.autonomy.searchcomponents.core.languages;

import com.hp.autonomy.types.idol.responses.LanguageType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@JsonTest
@AutoConfigureJsonTesters(enabled = false)
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
