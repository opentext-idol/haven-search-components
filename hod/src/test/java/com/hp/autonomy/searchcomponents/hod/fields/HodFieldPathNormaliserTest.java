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

package com.hp.autonomy.searchcomponents.hod.fields;

import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import org.junit.Before;
import org.junit.Test;

import static com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService.AUTN_DATE_FIELD;
import static org.junit.Assert.assertEquals;

public class HodFieldPathNormaliserTest {
    private FieldPathNormaliser fieldPathNormaliser;

    @Before
    public void setUp() {
        fieldPathNormaliser = new HodFieldPathNormaliserImpl();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArg() {
        fieldPathNormaliser.normaliseFieldPath(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyArg() {
        fieldPathNormaliser.normaliseFieldPath("");
    }

    @Test
    public void anyOldValue() {
        assertEquals("FOO", fieldPathNormaliser.normaliseFieldPath("FOO").getNormalisedPath());
    }

    @Test
    public void autnDate() {
        assertEquals(AUTN_DATE_FIELD.toLowerCase(), fieldPathNormaliser.normaliseFieldPath(AUTN_DATE_FIELD.toLowerCase()).getNormalisedPath());
    }

    @Test
    public void upperCaseAutnDate() {
        assertEquals(AUTN_DATE_FIELD.toLowerCase(), fieldPathNormaliser.normaliseFieldPath(AUTN_DATE_FIELD).getNormalisedPath());
    }
}
