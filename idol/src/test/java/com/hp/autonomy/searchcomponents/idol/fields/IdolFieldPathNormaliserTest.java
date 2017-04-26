/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.fields;

import com.hp.autonomy.searchcomponents.core.fields.FieldPathNormaliser;
import org.junit.Before;
import org.junit.Test;

import static com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService.AUTN_DATE_FIELD;
import static org.junit.Assert.assertEquals;

public class IdolFieldPathNormaliserTest {
    private static final String EXPECTED_NORMALISED_FIELD_PATH = "MY_FIELD";
    private static final String AMBIGUOUS_XML_FIELD_PATH = "MY_FIELD";
    private static final String EXPECTED_NORMALISED_XML_FIELD_PATH = "MY_FIELD1/MY_FIELD2";

    private FieldPathNormaliser fieldPathNormaliser;

    @Before
    public void setUp() {
        fieldPathNormaliser = new IdolFieldPathNormaliserImpl();
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
    public void autnDate() {
        assertEquals(AUTN_DATE_FIELD, fieldPathNormaliser.normaliseFieldPath(AUTN_DATE_FIELD).getNormalisedPath());
    }

    @Test
    public void lowerCaseAutnDate() {
        assertEquals(AUTN_DATE_FIELD, fieldPathNormaliser.normaliseFieldPath(AUTN_DATE_FIELD.toLowerCase()).getNormalisedPath());
    }

    @Test
    public void normalisedFullPathIdx() {
        assertEquals(EXPECTED_NORMALISED_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("/DOCUMENT/MY_FIELD").getNormalisedPath());
    }

    @Test
    public void fullPathIdx() {
        assertEquals(EXPECTED_NORMALISED_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("DOCUMENT/MY_FIELD").getNormalisedPath());
    }

    @Test
    public void nameWithSlashIdx() {
        assertEquals(EXPECTED_NORMALISED_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("/MY_FIELD").getNormalisedPath());
    }

    @Test
    public void nameOnlyIdx() {
        assertEquals(EXPECTED_NORMALISED_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("MY_FIELD").getNormalisedPath());
    }

    @Test
    public void normalisedFullPathIdxLowerCase() {
        assertEquals(EXPECTED_NORMALISED_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("/document/my_field").getNormalisedPath());
    }

    @Test
    public void fullPathIdxLowerCase() {
        assertEquals(EXPECTED_NORMALISED_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("document/my_field").getNormalisedPath());
    }

    @Test
    public void nameWithSlashIdxLowerCase() {
        assertEquals(EXPECTED_NORMALISED_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("/my_field").getNormalisedPath());
    }

    @Test
    public void nameOnlyIdxLowerCase() {
        assertEquals(EXPECTED_NORMALISED_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("my_field").getNormalisedPath());
    }

    @Test
    public void normalisedFullPathAmbiguousXml() {
        assertEquals(AMBIGUOUS_XML_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("/DOCUMENTS/DOCUMENT/MY_FIELD").getNormalisedPath());
    }

    @Test
    public void fullPathAmbiguousXml() {
        assertEquals(AMBIGUOUS_XML_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("DOCUMENTS/DOCUMENT/MY_FIELD").getNormalisedPath());
    }

    @Test
    public void normalisedFullPathAmbiguousXmlLowerCase() {
        assertEquals(AMBIGUOUS_XML_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("/documents/document/my_field").getNormalisedPath());
    }

    @Test
    public void fullPathAmbiguousXmlLowerCase() {
        assertEquals(AMBIGUOUS_XML_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("documents/document/my_field").getNormalisedPath());
    }

    @Test
    public void normalisedFullPathXml() {
        assertEquals(EXPECTED_NORMALISED_XML_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("/DOCUMENTS/DOCUMENT/MY_FIELD1/MY_FIELD2").getNormalisedPath());
    }

    @Test
    public void fullPathXml() {
        assertEquals(EXPECTED_NORMALISED_XML_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("DOCUMENTS/DOCUMENT/MY_FIELD1/MY_FIELD2").getNormalisedPath());
    }

    @Test
    public void normalisedPartialPathXml() {
        assertEquals(EXPECTED_NORMALISED_XML_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("/DOCUMENT/MY_FIELD1/MY_FIELD2").getNormalisedPath());
    }

    @Test
    public void partialPathXml() {
        assertEquals(EXPECTED_NORMALISED_XML_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("DOCUMENT/MY_FIELD1/MY_FIELD2").getNormalisedPath());
    }

    @Test
    public void nameWithSlashXml() {
        assertEquals(EXPECTED_NORMALISED_XML_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("/MY_FIELD1/MY_FIELD2").getNormalisedPath());
    }

    @Test
    public void nameOnlyXml() {
        assertEquals(EXPECTED_NORMALISED_XML_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("MY_FIELD1/MY_FIELD2").getNormalisedPath());
    }

    @Test
    public void normalisedFullPathXmlLowerCase() {
        assertEquals(EXPECTED_NORMALISED_XML_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("/documents/document/my_field1/my_field2").getNormalisedPath());
    }

    @Test
    public void fullPathXmlLowerCase() {
        assertEquals(EXPECTED_NORMALISED_XML_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("documents/document/my_field1/my_field2").getNormalisedPath());
    }

    @Test
    public void normalisedPartialPathXmlLowerCase() {
        assertEquals(EXPECTED_NORMALISED_XML_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("/document/my_field1/my_field2").getNormalisedPath());
    }

    @Test
    public void partialPathXmLowerCase() {
        assertEquals(EXPECTED_NORMALISED_XML_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("document/my_field1/my_field2").getNormalisedPath());
    }

    @Test
    public void nameWithSlashXmlLowerCase() {
        assertEquals(EXPECTED_NORMALISED_XML_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("/my_field1/my_field2").getNormalisedPath());
    }

    @Test
    public void nameOnlyXmlLowerCase() {
        assertEquals(EXPECTED_NORMALISED_XML_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("my_field1/my_field2").getNormalisedPath());
    }
}
