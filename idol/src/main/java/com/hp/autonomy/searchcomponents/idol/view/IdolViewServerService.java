/*
 * Copyright 2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
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
