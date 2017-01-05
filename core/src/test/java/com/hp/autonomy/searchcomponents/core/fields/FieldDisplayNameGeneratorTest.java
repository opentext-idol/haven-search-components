/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.fields;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FieldDisplayNameGeneratorTest {
    private FieldDisplayNameGenerator fieldDisplayNameGenerator;

    @Before
    public void setUp() {
        fieldDisplayNameGenerator = new FieldDisplayNameGeneratorImpl();
    }

    @Test
    public void prettifySimpleName() {
        assertEquals("Foo Bar", fieldDisplayNameGenerator.generateDisplayName("/DOCUMENT/FOO_BAR"));
    }

    @Test
    public void prettifySimpleNameLeadingUnderscore() {
        assertEquals("Foo Bar", fieldDisplayNameGenerator.generateDisplayName("/DOCUMENT/_FOO_BAR"));
    }

    @Test
    public void prettifySimpleNameInternalUnderscore() {
        assertEquals("Foo Bar", fieldDisplayNameGenerator.generateDisplayName("/DOCUMENT/FOO__BAR"));
    }

    @Test
    public void prettifySimpleNameTrailingUnderscore() {
        assertEquals("Foo Bar", fieldDisplayNameGenerator.generateDisplayName("/DOCUMENT/FOO_BAR_"));
    }

    @Test
    public void prettifyCompoundName() {
        assertEquals("Foo Baz", fieldDisplayNameGenerator.generateDisplayName("/DOCUMENTS/DOCUMENT/FOO_BAR/FOO_BAZ"));
    }
}
