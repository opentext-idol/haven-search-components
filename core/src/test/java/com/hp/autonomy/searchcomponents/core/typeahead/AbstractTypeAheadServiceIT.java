/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.typeahead;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
public class AbstractTypeAheadServiceIT<E extends Exception> {
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    protected TypeAheadService<E> typeAheadService;

    private final String inputText;
    private final String expectedSuggestion;

    public AbstractTypeAheadServiceIT(final String inputText, final String expectedSuggestion) {
        this.inputText = inputText;
        this.expectedSuggestion = expectedSuggestion;
    }

    @Test
    public void getsSuggestions() throws E {
        assertThat(typeAheadService.getSuggestions(inputText), hasItem(is(expectedSuggestion)));
    }
}
