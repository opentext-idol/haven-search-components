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

package com.hp.autonomy.searchcomponents.core.typeahead;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@AutoConfigureJson
public abstract class AbstractTypeAheadServiceIT<E extends Exception> {
    @Autowired
    protected TypeAheadService<E> typeAheadService;

    private final String inputText;
    private final String expectedSuggestion;

    protected AbstractTypeAheadServiceIT(final String inputText, final String expectedSuggestion) {
        this.inputText = inputText;
        this.expectedSuggestion = expectedSuggestion;
    }

    @Test
    public void getsSuggestions() throws E {
        assertThat(typeAheadService.getSuggestions(inputText), hasItem(is(expectedSuggestion)));
    }
}
