/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.exceptions;

import com.autonomy.aci.client.services.AciErrorException;

/**
 * Interface defining an aspect which maps {@link AciErrorException} with recognised error id to an enumerated exception
 */
@FunctionalInterface
public interface AciErrorExceptionAspect {
    void mapExceptions(final IdolService idolService, final AciErrorException e);
}
