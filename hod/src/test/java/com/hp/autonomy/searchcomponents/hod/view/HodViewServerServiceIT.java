/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.view;

import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.view.AbstractViewServerServiceIT;
import com.hp.autonomy.searchcomponents.hod.beanconfiguration.HavenSearchHodConfiguration;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictions;
import com.hp.autonomy.searchcomponents.hod.search.HodSearchResult;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HavenSearchHodConfiguration.class)
public class HodViewServerServiceIT extends AbstractViewServerServiceIT<HodViewRequest, HodQueryRestrictions, ResourceName, HodSearchResult, HodErrorException> {
}