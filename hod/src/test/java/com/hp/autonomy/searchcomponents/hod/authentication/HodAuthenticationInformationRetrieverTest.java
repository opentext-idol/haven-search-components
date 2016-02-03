/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.authentication;

import com.hp.autonomy.hod.sso.HodAuthentication;
import com.hp.autonomy.searchcomponents.core.authentication.AbstractAuthenticationInformationRetrieverTest;
import org.junit.Before;
import org.mockito.Mock;

public class HodAuthenticationInformationRetrieverTest extends AbstractAuthenticationInformationRetrieverTest<HodAuthentication> {
    @Mock
    private HodAuthentication hodAuthentication;

    @Before
    public void setUp() {
        authenticationInformationRetriever = new HodAuthenticationInformationRetriever();
        authentication = hodAuthentication;
    }
}
