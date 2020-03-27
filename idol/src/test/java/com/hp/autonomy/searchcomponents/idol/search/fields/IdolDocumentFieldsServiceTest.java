/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search.fields;

import com.hp.autonomy.searchcomponents.core.search.fields.AbstractDocumentFieldsServiceTest;
import com.hp.autonomy.searchcomponents.core.test.CoreTestContext;
import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;

import static com.hp.autonomy.searchcomponents.core.test.CoreTestContext.CORE_CLASSES_PROPERTY;

@SpringBootTest(classes = {CoreTestContext.class, IdolDocumentFieldsServiceImpl.class}, properties = CORE_CLASSES_PROPERTY, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class IdolDocumentFieldsServiceTest extends AbstractDocumentFieldsServiceTest {
    @Before
    public void setUp() {
        numberOfHardCodedFields = 4;
    }
}
