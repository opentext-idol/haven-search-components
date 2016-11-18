/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.view;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.view.AbstractViewServerServiceIT;
import com.hp.autonomy.searchcomponents.idol.beanconfiguration.HavenSearchIdolConfiguration;
import com.hp.autonomy.searchcomponents.idol.search.IdolSearchResult;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HavenSearchIdolConfiguration.class)
public class IdolViewServerServiceIT extends AbstractViewServerServiceIT<IdolViewRequest, String, IdolSearchResult, AciErrorException> {
}