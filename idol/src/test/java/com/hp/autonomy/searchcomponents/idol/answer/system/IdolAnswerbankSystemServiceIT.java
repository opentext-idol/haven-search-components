/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.system;

import com.autonomy.aci.client.transport.AciServerDetails;
import com.hp.autonomy.searchcomponents.idol.beanconfiguration.HavenSearchIdolConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

import static com.hp.autonomy.searchcomponents.idol.test.IdolTestConfiguration.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HavenSearchIdolConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class IdolAnswerbankSystemServiceIT {
    @Autowired
    private IdolAnswerbankSystemService answerbankSystemService;

    @Value("${" + ANSWER_SERVER_HOST_PROPERTY + ':' + ANSWER_SERVER_HOST + '}')
    private String host;

    @Value("${" + ANSWER_SERVER_PORT_PROPERTY + ':' + ANSWER_SERVER_PORT + '}')
    private int port;

    @Value("${" + ANSWER_SERVER_SYSTEM_PROPERTY + ':' + ANSWER_SERVER_SYSTEM_NAME + '}')
    private String systemName;

    @Test
    public void getSystemNames() {
        final Collection<String> systemNames = answerbankSystemService.getSystemNames(new AciServerDetails(AciServerDetails.TransportProtocol.HTTP, host, port));
        assertThat(systemNames, hasItem(systemName));
    }

    @Test
    public void getConfiguredSystemNames() {
        final Collection<String> systemNames = answerbankSystemService.getSystemNames();
        assertThat(systemNames, hasItem(systemName));
    }
}
