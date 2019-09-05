package com.hp.autonomy.searchcomponents.core.config;

import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class FieldTypeTest {

    @Test
    public void parseValue_date() {
        final ZonedDateTime expected =
            ZonedDateTime.ofInstant(Instant.ofEpochSecond(946782245), ZoneOffset.UTC);
        final ZonedDateTime result =
            FieldType.DATE.parseValue(ZonedDateTime.class, "2000-01-02T03:04:05Z");
        Assert.assertEquals(expected, result);
    }

    @Test
    public void parseValue_invalidDate() {
        Assert.assertNull(FieldType.DATE.parseValue(ZonedDateTime.class, "invalid"));
    }

}
