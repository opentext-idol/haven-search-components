/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.ask;

import com.hp.autonomy.searchcomponents.idol.beanconfiguration.HavenSearchIdolConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HavenSearchIdolConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AskAnswerServerServiceIT {
    @Autowired
    private AskAnswerServerService service;

    @Autowired
    private ObjectFactory<AskAnswerServerRequestBuilder> requestBuilderFactory;

    @Test
    public void ask() {
        final AskAnswerServerRequest request = requestBuilderFactory.getObject()
                .text("GPU")
                .build();
        // not currently checking anything other than failure to throw an error as don't want to make assumptions about data
        service.ask(request);
    }
}
