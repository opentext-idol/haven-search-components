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
    private static final String EXPECTED_NORMALISED_FIELD_PATH = "/DOCUMENT/MY_FIELD";
    private static final String EXPECTED_NORMALISED_XML_FIELD_PATH = "/DOCUMENTS/DOCUMENT/MY_FIELD1/MY_FIELD2";

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
        assertEquals(AUTN_DATE_FIELD, fieldPathNormaliser.normaliseFieldPath(AUTN_DATE_FIELD));
    }

    @Test
    public void lowerCaseAutnDate() {
        assertEquals(AUTN_DATE_FIELD, fieldPathNormaliser.normaliseFieldPath(AUTN_DATE_FIELD.toLowerCase()));
    }

    @Test
    public void normalisedFullPathIdx() {
        assertEquals(EXPECTED_NORMALISED_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("/DOCUMENT/MY_FIELD"));
    }

    @Test
    public void fullPathIdx() {
        assertEquals(EXPECTED_NORMALISED_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("DOCUMENT/MY_FIELD"));
    }

    @Test
    public void nameWithSlashIdx() {
        assertEquals(EXPECTED_NORMALISED_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("/MY_FIELD"));
    }

    @Test
    public void nameOnlyIdx() {
        assertEquals(EXPECTED_NORMALISED_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("MY_FIELD"));
    }

    @Test
    public void normalisedFullPathIdxLowerCase() {
        assertEquals(EXPECTED_NORMALISED_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("/document/my_field"));
    }

    @Test
    public void fullPathIdxLowerCase() {
        assertEquals(EXPECTED_NORMALISED_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("document/my_field"));
    }

    @Test
    public void nameWithSlashIdxLowerCase() {
        assertEquals(EXPECTED_NORMALISED_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("/my_field"));
    }

    @Test
    public void nameOnlyIdxLowerCase() {
        assertEquals(EXPECTED_NORMALISED_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("my_field"));
    }

    @Test
    public void normalisedFullPathXml() {
        assertEquals(EXPECTED_NORMALISED_XML_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("/DOCUMENTS/DOCUMENT/MY_FIELD1/MY_FIELD2"));
    }

    @Test
    public void fullPathXml() {
        assertEquals(EXPECTED_NORMALISED_XML_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("DOCUMENTS/DOCUMENT/MY_FIELD1/MY_FIELD2"));
    }

    @Test
    public void nameWithSlashXml() {
        assertEquals("/DOCUMENT/MY_FIELD1/MY_FIELD2", fieldPathNormaliser.normaliseFieldPath("/MY_FIELD1/MY_FIELD2"));
    }

    @Test
    public void nameOnlyXml() {
        assertEquals("/DOCUMENT/MY_FIELD1/MY_FIELD2", fieldPathNormaliser.normaliseFieldPath("MY_FIELD1/MY_FIELD2"));
    }

    @Test
    public void normalisedFullPathXmlLowerCase() {
        assertEquals(EXPECTED_NORMALISED_XML_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("/documents/document/my_field1/my_field2"));
    }

    @Test
    public void fullPathXmlLowerCase() {
        assertEquals(EXPECTED_NORMALISED_XML_FIELD_PATH, fieldPathNormaliser.normaliseFieldPath("documents/document/my_field1/my_field2"));
    }

    @Test
    public void nameWithSlashXmlLowerCase() {
        assertEquals("/DOCUMENT/MY_FIELD1/MY_FIELD2", fieldPathNormaliser.normaliseFieldPath("/my_field1/my_field2"));
    }

    @Test
    public void nameOnlyXmlLowerCase() {
        assertEquals("/DOCUMENT/MY_FIELD1/MY_FIELD2", fieldPathNormaliser.normaliseFieldPath("my_field1/my_field2"));
    }
}
