package com.hp.autonomy.searchcomponents.hod.authentication;

import com.hp.autonomy.hod.sso.HodUsernameResolver;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Component
class HodUsernameResolverImpl implements HodUsernameResolver {
    @Override
    public String resolve(final Map<String, Serializable> metadata) {
        final Serializable serializableName = metadata.get(metadata.get(HavenSearchUserMetadata.USER_DISPLAY_NAME) != null
                ? HavenSearchUserMetadata.USER_DISPLAY_NAME
                : HavenSearchUserMetadata.LEGACY_USER_DISPLAY_NAME);

        return serializableName instanceof String ? (String) serializableName : null;
    }
}
