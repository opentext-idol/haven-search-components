/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.idol.exceptions;

import com.autonomy.aci.client.services.AciErrorException;
import com.hp.autonomy.searchcomponents.idol.annotations.IdolService;
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
