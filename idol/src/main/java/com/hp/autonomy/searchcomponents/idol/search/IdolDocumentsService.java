/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.search.DocumentsService;

/**
 * Idol extension to {@link DocumentsService}
 */
public interface IdolDocumentsService extends DocumentsService<IdolQueryRequest, IdolSuggestRequest, IdolGetContentRequest, IdolQueryRestrictions, IdolSearchResult, AciErrorException> {
}
