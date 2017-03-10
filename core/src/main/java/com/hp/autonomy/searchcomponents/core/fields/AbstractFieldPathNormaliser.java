package com.hp.autonomy.searchcomponents.core.fields;

import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;

/**
 * This minimal abstract class allows FieldPathNormaliser implementations to access package-private {@link FieldPathImpl}
 */
public abstract class AbstractFieldPathNormaliser implements FieldPathNormaliser {
    protected FieldPath newFieldPath(final String normalisedPath, final String path) {
        return new FieldPathImpl(normalisedPath, path);
    }
}
