/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.typeahead;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
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
