/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.exceptions;

import com.autonomy.aci.client.services.AciErrorException;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Default implementation of {@link AciErrorExceptionAspect}
 */
@Aspect
@Component
class AciErrorExceptionAspectImpl implements AciErrorExceptionAspect {
    @Override
    @AfterThrowing(pointcut = "@within(idolService)", throwing = "e")
    public void mapExceptions(final IdolService idolService, final AciErrorException e) {
        final Optional<IdolErrorCode<?>> errorCode = idolService.value().forErrorId(e.getErrorId());
        if(errorCode.isPresent()) {
            throw new EnumeratedAciErrorException(e.getMessage(), e, errorCode.get());
        } else {
            throw e;
        }
    }
}
