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

package com.hp.autonomy.searchcomponents.hod.search.fields;

import com.hp.autonomy.searchcomponents.core.search.fields.AbstractDocumentFieldsServiceTest;
import com.hp.autonomy.searchcomponents.core.test.CoreTestContext;
import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;

import static com.hp.autonomy.searchcomponents.core.test.CoreTestContext.CORE_CLASSES_PROPERTY;

@SpringBootTest(classes = {CoreTestContext.class, HodDocumentFieldsService.class}, properties = CORE_CLASSES_PROPERTY, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class HodDocumentFieldsServiceTest extends AbstractDocumentFieldsServiceTest {
    @Before
    public void setUp() {
        numberOfHardCodedFields = 0;
    }
}
