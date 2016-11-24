/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.search.RelatedConceptsService;
import com.hp.autonomy.types.idol.responses.QsElement;

/**
 * Idol extension to {@link RelatedConceptsService}
 */
@FunctionalInterface
public interface IdolRelatedConceptsService extends RelatedConceptsService<QsElement, String, AciErrorException> {
}
