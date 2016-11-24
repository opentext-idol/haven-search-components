/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.view;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.core.view.ViewServerService;

/**
 * Idol extension to {@link ViewServerService}
 */
public interface IdolViewServerService extends ViewServerService<IdolViewRequest, String, AciErrorException> {
    /**
     * Connector "identifier" field
     */
    String AUTN_IDENTIFIER = "AUTN_IDENTIFIER";

    /**
     * Connector "group" field
     */
    String AUTN_GROUP = "AUTN_GROUP";
}
