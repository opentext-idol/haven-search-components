/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.view;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.searchcomponents.core.view.AbstractViewServerServiceIT;
import com.hp.autonomy.searchcomponents.idol.beanconfiguration.HavenSearchIdolConfiguration;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HavenSearchIdolConfiguration.class)
public class IdolViewServerServiceIT extends AbstractViewServerServiceIT<String, SearchResult, AciErrorException> {
}