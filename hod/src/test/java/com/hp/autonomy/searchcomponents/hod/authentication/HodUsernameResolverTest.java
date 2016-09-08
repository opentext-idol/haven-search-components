package com.hp.autonomy.searchcomponents.hod.authentication;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.autonomy.hod.sso.HodUserMetadata;
import com.hp.autonomy.hod.sso.HodUserMetadataResolver;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HodUsernameResolverTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void usesCorrectDisplayNameOverLegacyDisplayName() throws IOException {
        final HodUserMetadataResolver hsodUsernameResolver = new HsodUsernameResolver();
        final Map<String, JsonNode> metadataMap = readInJsonFile("/authentication/user-metadata/metadata.json");
        final HodUserMetadata returnedMetadata = hsodUsernameResolver.resolve(metadataMap);

        assertEquals("correct name", returnedMetadata.getUserDisplayName());
        assertThat(returnedMetadata.getMetadata().size(), is(0));
    }

    @Test
    public void usesCorrectLegacyDisplayName() throws IOException {
        final HodUserMetadataResolver hsodUsernameResolver = new HsodUsernameResolver();
        final Map<String, JsonNode> metadataMap = readInJsonFile("/authentication/user-metadata/metadata-with-legacy-displayname.json");
        final HodUserMetadata returnedMetadata = hsodUsernameResolver.resolve(metadataMap);

        assertEquals("legacy name", returnedMetadata.getUserDisplayName());
        assertThat(returnedMetadata.getMetadata().size(), is(0));
    }


    @Test
    public void usesCorrectDisplayNameFromArrayOverLegacyDisplayName() throws IOException {
        final HodUserMetadataResolver hsodUsernameResolver = new HsodUsernameResolver();
        final Map<String, JsonNode> metadataMap = readInJsonFile("/authentication/user-metadata/metadata-with-displayname-array.json");
        final HodUserMetadata returnedMetadata = hsodUsernameResolver.resolve(metadataMap);

        assertEquals("correct name", returnedMetadata.getUserDisplayName());
        assertThat(returnedMetadata.getMetadata().size(), is(0));
    }

    @Test
    public void noUserInformation() throws IOException {
        final HodUserMetadataResolver hsodUsernameResolver = new HsodUsernameResolver();
        final Map<String, JsonNode> metadata = new HashMap<>();
        final HodUserMetadata returnedMetadata = hsodUsernameResolver.resolve(metadata);
        assertNull(returnedMetadata.getUserDisplayName());
    }

    private Map<String, JsonNode> readInJsonFile(final String pathname) throws IOException {
        return objectMapper.readValue(HodUsernameResolverTest.class.getResourceAsStream(pathname), new TypeReference<Map<String, JsonNode>>() {});
    }

}
