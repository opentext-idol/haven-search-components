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

package com.hp.autonomy.searchcomponents.idol.answer.configuration;

import com.autonomy.aci.client.transport.AciServerDetails;
import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.ConfigurationComponentTest;
import com.hp.autonomy.frontend.configuration.server.ServerConfig;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

public class AnswerServerConfigTest extends ConfigurationComponentTest<AnswerServerConfig> {
    @Test(expected = ConfigException.class)
    public void noServer() throws ConfigException {
        AnswerServerConfig.builder()
                .systemName("something")
                .enabled(true)
                .build()
                .basicValidate(null);
    }

    @Test(expected = ConfigException.class)
    public void invalidServer() throws ConfigException {
        AnswerServerConfig.builder()
                .server(ServerConfig.builder().build())
                .systemName("something")
                .enabled(true)
                .build()
                .basicValidate(null);
    }

    @Test
    public void invalidButDisabled() throws ConfigException {
        AnswerServerConfig.builder()
                .build()
                .basicValidate(null);
    }

    @Override
    protected Class<AnswerServerConfig> getType() {
        return AnswerServerConfig.class;
    }

    @Override
    protected AnswerServerConfig constructComponent() {
        return AnswerServerConfig.builder()
                .server(ServerConfig.builder()
                        .host("localhost")
                        .port(7000)
                        .build())
                .systemName("answerbank1")
                .enabled(true)
                .build();
    }

    @Override
    protected String sampleJson() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/searchcomponents/idol/answer/configuration/answer-server.json"));
    }

    @Override
    protected void validateJson(final JsonContent<AnswerServerConfig> jsonContent) {
        jsonContent.assertThat().hasJsonPathStringValue("@.server.host", "localhost");
        jsonContent.assertThat().hasJsonPathNumberValue("@.server.port", 7000);
        jsonContent.assertThat().hasJsonPathStringValue("@.systemNames[0]", "answerbank1");
        jsonContent.assertThat().hasJsonPathBooleanValue("@.enabled", true);
    }

    @Override
    protected void validateParsedComponent(final ObjectContent<AnswerServerConfig> objectContent) {
        assertThat(objectContent.getObject().getServer().getProtocol(), is(AciServerDetails.TransportProtocol.HTTP));
        assertThat(objectContent.getObject().getServer().getHost(), is("localhost"));
        assertThat(objectContent.getObject().getServer().getPort(), is(7700));
        assertThat(objectContent.getObject().getServer().getProductType(), hasSize(1));
        assertThat(objectContent.getObject().getSystemNames(), hasSize(1));
        objectContent.assertThat().hasFieldOrPropertyWithValue("enabled", true);
    }

    @Override
    protected void validateMergedComponent(final ObjectContent<AnswerServerConfig> objectContent) {
        assertThat(objectContent.getObject().getServer().getProtocol(), is(AciServerDetails.TransportProtocol.HTTP));
        assertThat(objectContent.getObject().getServer().getHost(), is("localhost"));
        assertThat(objectContent.getObject().getServer().getPort(), is(7000));
        assertThat(objectContent.getObject().getServer().getProductType(), hasSize(1));
        assertThat(objectContent.getObject().getSystemNames(), hasSize(1));
        objectContent.assertThat().hasFieldOrPropertyWithValue("enabled", true);
    }

    @Override
    protected void validateString(final String s) {
        assertTrue(s.contains("answerbank"));
    }
}
