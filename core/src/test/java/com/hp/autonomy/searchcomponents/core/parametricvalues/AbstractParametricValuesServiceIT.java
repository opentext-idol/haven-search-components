/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.hp.autonomy.searchcomponents.core.test.TestUtils;
import com.hp.autonomy.types.idol.RecursiveField;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractParametricValuesServiceIT<R extends ParametricRequest<S>, S extends Serializable, E extends Exception> {
    @Autowired
    private ParametricValuesService<R, S, E> parametricValuesService;

    @Autowired
    protected TestUtils<S> testUtils;

    protected abstract R createParametricRequest();

    @Test
    public void getAllParametricValues() throws E {
        final Set<QueryTagInfo> results = parametricValuesService.getAllParametricValues(createParametricRequest());
        assertThat(results, is(not(empty())));
    }

    @Test
    public void getDependentParametricValues() throws E {
        final Collection<RecursiveField> results = parametricValuesService.getDependentParametricValues(createParametricRequest());
        assertThat(results, is(not(empty())));
    }
}
