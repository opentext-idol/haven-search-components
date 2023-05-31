/*
 * Copyright 2018 Open Text.
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

package com.hp.autonomy.searchcomponents.idol.logging;

import com.autonomy.aci.client.transport.AciServerDetails;
import com.autonomy.aci.client.transport.ActionParameter;
import com.autonomy.aci.client.util.ActionParameters;
import java.util.Collection;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

import static com.hp.autonomy.searchcomponents.idol.logging.IdolLoggingAspect.LOGGING_PRECEDENCE;

@SuppressWarnings("ProhibitedExceptionDeclared")
@Slf4j
@Aspect
// This has to come before the logger, so that the ActionId is logged.
@Order(LOGGING_PRECEDENCE - 1)
public class IdolActionIdAspect {

    private static final String ACTION_ID_PARAM = "ActionId";

    private final String prefix;

    public IdolActionIdAspect(final String prefix) {
        this.prefix = prefix;
    }

    @Around(value = "execution(* com.autonomy.aci.client.transport.AciHttpClient.executeAction(..)) && args(serverDetails, parameters)",
            argNames = "joinPoint,serverDetails,parameters")
    public Object addActionIdParameter(
            final ProceedingJoinPoint joinPoint,
            final AciServerDetails serverDetails,
            final Collection<? extends ActionParameter<?>> parameters) throws Throwable {
        if (parameters.stream().noneMatch(param -> ACTION_ID_PARAM.equalsIgnoreCase(param.getName()))) {
            final ActionParameters newParams = new ActionParameters(parameters);
            newParams.add(ACTION_ID_PARAM, prefix + UUID.randomUUID().toString());
            return joinPoint.proceed(new Object[]{serverDetails, newParams});
        }
        else {
            return joinPoint.proceed();
        }
    }
}
