/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.test;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.test.IntegrationTestUtils;
import com.hp.autonomy.searchcomponents.idol.search.IdolSearchResult;
import org.springframework.stereotype.Component;

@Component
public class IdolIntegrationTestUtils extends IntegrationTestUtils<String, IdolSearchResult, AciErrorException> {
}
