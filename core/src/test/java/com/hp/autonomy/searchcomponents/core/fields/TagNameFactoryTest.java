package com.hp.autonomy.searchcomponents.core.fields;

import com.hp.autonomy.searchcomponents.core.config.FieldType;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unused")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TagNameFactoryImpl.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TagNameFactoryTest {
    @MockBean
    private FieldPathNormaliser fieldPathNormaliser;
    @MockBean
    private FieldDisplayNameGenerator fieldDisplayNameGenerator;
    @Autowired
    private TagNameFactory tagNameFactory;

    @Test
    public void buildTagName() {
        final String path = "SOME_PATH";
        assertNotNull(tagNameFactory.buildTagName(path));
        verify(fieldPathNormaliser).normaliseFieldPath(path);
        verify(fieldDisplayNameGenerator).generateDisplayName(any());
    }

    @Test
    public void getFieldValue() {
        final String path = "SOME_PATH";
        tagNameFactory.getFieldPath(path);
        verify(fieldPathNormaliser).normaliseFieldPath(path);
    }

    @Test
    public void getTagDisplayValue() {
        final String field = "SOME_FIELD";
        final FieldPath normalisedField = new FieldPathImpl("/DOCUMENT/SOME_FIELD", "SOME_FIELD");
        when(fieldPathNormaliser.normaliseFieldPath(field)).thenReturn(normalisedField);
        final String value = "SOME_VALUE";
        final String displayValue = "Some Value";
        when(fieldDisplayNameGenerator.generateDisplayValue(normalisedField, value, FieldType.STRING)).thenReturn(displayValue);
        assertEquals(displayValue, tagNameFactory.getTagDisplayValue(field, value));
    }
}
