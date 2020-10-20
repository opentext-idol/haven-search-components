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

package com.hp.autonomy.searchcomponents.hod.typeahead;

import com.hp.autonomy.hod.client.api.analysis.autocomplete.AutocompleteService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HodTypeAheadServiceTest {
    @Mock
    private AutocompleteService autocompleteService;

    private TypeAheadService<HodErrorException> typeAheadService;

    @Before
    public void setUp() {
        typeAheadService = new HodTypeAheadServiceImpl(autocompleteService);
    }

    @Test
    public void getSuggestions() throws HodErrorException {
        when(autocompleteService.getSuggestions("badge")).thenReturn(Collections.singletonList("badger"));
        assertThat(typeAheadService.getSuggestions("badge"), hasItem(is("badger")));
    }

    @Test
    public void getSuggestionsNoText() throws HodErrorException {
        assertThat(typeAheadService.getSuggestions(null), is(empty()));
    }
}
