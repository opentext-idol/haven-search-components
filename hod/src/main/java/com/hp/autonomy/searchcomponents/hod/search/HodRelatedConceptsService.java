/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.search;

import com.hp.autonomy.hod.client.api.textindex.query.search.Entity;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsService;

/**
 * HoD extension to {@link RelatedConceptsService}
 */
@FunctionalInterface
public interface HodRelatedConceptsService extends RelatedConceptsService<HodRelatedConceptsRequest, Entity, HodQueryRestrictions, HodErrorException> {
}
