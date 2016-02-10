/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.fields;

import com.hp.autonomy.types.requests.idol.actions.tags.TagResponse;

import java.util.List;

public interface FieldsService<R extends FieldsRequest, E extends Exception> {

    List<String> getParametricFields(final R request) throws E;

    TagResponse getFields(final R request, final String... fieldTypes) throws E;

}