/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search.fields;

import com.hp.autonomy.searchcomponents.core.search.fields.AbstractDocumentFieldsServiceTest;
import org.junit.Before;

public class IdolDocumentFieldsServiceTest extends AbstractDocumentFieldsServiceTest {
    @Override
    @Before
    public void setUp() {
        documentFieldsService = new IdolDocumentFieldsService(configService);
        numberOfHardCodedFields = 2;
        super.setUp();
    }
}
