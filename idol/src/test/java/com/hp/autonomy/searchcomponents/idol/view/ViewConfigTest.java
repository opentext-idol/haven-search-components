/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.view;

import com.autonomy.aci.client.transport.AciServerDetails;
import com.hp.autonomy.frontend.configuration.ConfigurationComponentTest;
import com.hp.autonomy.frontend.configuration.server.ServerConfig;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewConfig;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewingMode;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import java.io.IOException;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

public class ViewConfigTest extends ConfigurationComponentTest<ViewConfig> {
    @Override
    protected Class<ViewConfig> getType() {
        return ViewConfig.class;
    }

    @Override
    protected ViewConfig constructComponent() {
        return ViewConfig.builder()
                .protocol(AciServerDetails.TransportProtocol.HTTPS)
                .host("localhost")
                .port(9080)
                .viewingMode(ViewingMode.CONNECTOR)
                .highlighting(false)
                .build();
    }

    @Override
    protected String sampleJson() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/searchcomponents/idol/configuration/view.json"));
    }

    @Override
    protected void validateJson(final JsonContent<ViewConfig> jsonContent) {
        jsonContent.assertThat().hasJsonPathStringValue("protocol", "HTTPS");
        jsonContent.assertThat().hasJsonPathStringValue("host", "localhost");
        jsonContent.assertThat().hasJsonPathNumberValue("port", 9080);
        jsonContent.assertThat().hasJsonPathStringValue("viewingMode", "CONNECTOR");
        jsonContent.assertThat().hasJsonPathBooleanValue("highlighting", false);
    }

    @Override
    protected void validateParsedComponent(final ObjectContent<ViewConfig> objectContent) {
        objectContent.assertThat().hasFieldOrPropertyWithValue("protocol", AciServerDetails.TransportProtocol.HTTP);
        objectContent.assertThat().hasFieldOrPropertyWithValue("host", "localhost");
        objectContent.assertThat().hasFieldOrPropertyWithValue("port", 9080);
        objectContent.assertThat().hasFieldOrPropertyWithValue("referenceField", "DREREFERENCE");
        objectContent.assertThat().hasFieldOrPropertyWithValue("viewingMode", ViewingMode.FIELD);
        objectContent.assertThat().hasFieldOrPropertyWithValue("highlighting", true);
        assertThat(objectContent.getObject().getProductType(), hasSize(3));
        final ServerConfig connector = objectContent.getObject().getConnector();
        assertThat(connector.getProtocol(), is(AciServerDetails.TransportProtocol.HTTP));
        assertThat(connector.getHost(), is("localhost"));
        assertThat(connector.getPort(), is(10000));
        assertThat(Objects.toString(connector.getProductTypeRegex()), is(".*?CONNECTOR"));
    }

    @Override
    protected void validateMergedComponent(final ObjectContent<ViewConfig> objectContent) {
        objectContent.assertThat().hasFieldOrPropertyWithValue("protocol", AciServerDetails.TransportProtocol.HTTPS);
        objectContent.assertThat().hasFieldOrPropertyWithValue("host", "localhost");
        objectContent.assertThat().hasFieldOrPropertyWithValue("port", 9080);
        objectContent.assertThat().hasFieldOrPropertyWithValue("referenceField", "DREREFERENCE");
        objectContent.assertThat().hasFieldOrPropertyWithValue("viewingMode", ViewingMode.CONNECTOR);
        objectContent.assertThat().hasFieldOrPropertyWithValue("highlighting", false);
        assertThat(objectContent.getObject().getProductType(), hasSize(3));
        final ServerConfig connector = objectContent.getObject().getConnector();
        assertThat(connector.getProtocol(), is(AciServerDetails.TransportProtocol.HTTP));
        assertThat(connector.getHost(), is("localhost"));
        assertThat(connector.getPort(), is(10000));
        assertThat(Objects.toString(connector.getProductTypeRegex()), is(".*?CONNECTOR"));
    }

    @Override
    protected void validateString(final String objectAsString) {
        assertTrue(objectAsString.contains("connector"));
    }
}
