/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.typeahead;

import com.hp.autonomy.hod.client.api.analysis.autocomplete.AutocompleteService;
import com.hp.autonomy.searchcomponents.core.typeahead.GetSuggestionsFailedException;
import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

@RunWith(MockitoJUnitRunner.class)
public class HodTypeAheadServiceTest {
    @Mock
    private AutocompleteService autocompleteService;

    private TypeAheadService typeAheadService;

    @Before
    public void setUp() {
        typeAheadService = new HodTypeAheadService(autocompleteService);
    }

    @Test
    public void noText() throws GetSuggestionsFailedException {
        assertThat(typeAheadService.getSuggestions(null), is(empty()));
    }
}
