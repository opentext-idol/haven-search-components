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

package com.hp.autonomy.searchcomponents.idol.custom;

import com.autonomy.aci.client.services.AciService;
import com.hp.autonomy.frontend.configuration.validation.Validator;
import com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService;
import com.hp.autonomy.searchcomponents.idol.answer.configuration.AnswerServerConfig;
import com.hp.autonomy.searchcomponents.idol.beanconfiguration.HavenSearchIdolConfiguration;
import com.hp.autonomy.searchcomponents.idol.databases.IdolDatabasesService;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictionsBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.hp.autonomy.searchcomponents.core.test.TestUtils.CUSTOMISATION_TEST_ID;
import static com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable.ANSWER_SERVER_VALIDATOR_BEAN_NAME;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {HavenSearchIdolConfiguration.class, IdolCustomConfiguration.class, IdolCustomComponentConfiguration.class}, properties = CUSTOMISATION_TEST_ID)
public class IdolCustomisationTest {
    @Autowired
    @Qualifier("customDatabaseService")
    private IdolDatabasesService customDatabaseService;
    @Autowired
    @Qualifier("customQueryRestrictionsBuilder")
    private ObjectFactory<IdolQueryRestrictionsBuilder> customQueryRestrictionsBuilderFactory;
    @Autowired
    @Qualifier(ANSWER_SERVER_VALIDATOR_BEAN_NAME)
    private Validator<AnswerServerConfig> answerServerConfigValidator;
    @Autowired
    @Qualifier("customDocumentFieldsService")
    private DocumentFieldsService customDocumentFieldsService;
    @Autowired
    @Qualifier("contentAciService")
    private AciService contentAciService;

    @Test
    public void initialiseWithCustomConfiguration() {
        assertNotNull(customDatabaseService);
        assertNotNull(customQueryRestrictionsBuilderFactory.getObject());
        assertNotNull(customDocumentFieldsService);
        assertNull(answerServerConfigValidator.validate(null)); // verify it is a mock
        assertNull(contentAciService.executeAction(null, null)); // verify it is a mock
    }
}
