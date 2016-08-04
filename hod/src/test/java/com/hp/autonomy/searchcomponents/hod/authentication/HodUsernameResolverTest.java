package com.hp.autonomy.searchcomponents.hod.authentication;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

public class HodUsernameResolverTest {
    @Test
    public void usesCorrectDisplayName() {
        final Map<String, Serializable> metadata = ImmutableMap.of(HavenSearchUserMetadata.USER_DISPLAY_NAME, "James Bond");

        final HodUsernameResolverImpl hodUsernameResolverImpl = new HodUsernameResolverImpl();

        final String displayName = hodUsernameResolverImpl.resolve(metadata);
        assertEquals("James Bond", displayName);
    }

    @Test
    public void usesCorrectLegacyDisplayName() {
        final Map<String, Serializable> metadata = ImmutableMap.of(HavenSearchUserMetadata.LEGACY_USER_DISPLAY_NAME, "Jimmy Bond");

        final HodUsernameResolverImpl hodUsernameResolverImpl = new HodUsernameResolverImpl();

        final String displayName = hodUsernameResolverImpl.resolve(metadata);
        assertEquals("Jimmy Bond", displayName);
    }

    @Test
    public void usesCorrectDisplayNameOverLegacyDisplayName() {
        final Map<String, Serializable> metadata = ImmutableMap.of(
                HavenSearchUserMetadata.USER_DISPLAY_NAME, "James Bond",
                HavenSearchUserMetadata.LEGACY_USER_DISPLAY_NAME, "Jimmy Bond");

        final HodUsernameResolverImpl hodUsernameResolverImpl = new HodUsernameResolverImpl();

        final String displayName = hodUsernameResolverImpl.resolve(metadata);
        assertEquals("James Bond", displayName);
    }

    @Test
    public void noUserInformation() {
        final HodUsernameResolverImpl hodUsernameResolverImpl = new HodUsernameResolverImpl();

        final String displayName = hodUsernameResolverImpl.resolve(Collections.emptyMap());
        assertNull(displayName);
    }

    @Test
    public void nonStringUserInformation() {
        final Map<String, Serializable> metadata = ImmutableMap.of(HavenSearchUserMetadata.USER_DISPLAY_NAME, mock(Serializable.class));

        final HodUsernameResolverImpl hodUsernameResolverImpl = new HodUsernameResolverImpl();

        final String displayName = hodUsernameResolverImpl.resolve(metadata);
        assertNull(displayName);
    }
}
