package com.hp.autonomy.searchcomponents.idol.logging;

import com.autonomy.aci.client.transport.AciParameter;
import com.autonomy.aci.client.transport.AciResponseInputStream;
import com.autonomy.aci.client.transport.AciServerDetails;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import com.hp.autonomy.types.requests.idol.actions.user.params.SecurityParams;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.util.StopWatch;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Intercepts and logs all calls to Idol.
 */
@SuppressWarnings("ProhibitedExceptionDeclared")
@Slf4j
@Aspect
public class IdolLoggingAspect {
    private static final Marker IDOL = MarkerFactory.getMarker("IDOL");

    private static final String PARAMETER_SEPARATOR = "&";
    private static final char NAME_VALUE_SEPARATOR = '=';
    private static final String HIDDEN_VALUE = "*******";

    private final ConfigService<? extends IdolSearchCapable> configService;
    private final boolean timingEnabled;

    public IdolLoggingAspect(final ConfigService<? extends IdolSearchCapable> configService,
                             final boolean timingEnabled) {
        this.configService = configService;
        this.timingEnabled = timingEnabled;
    }

    private Object timeIdolRequests(final ProceedingJoinPoint joinPoint, final StringBuilder messageBuilder) throws Throwable {
        if (timingEnabled) {
            final StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            try {
                return joinPoint.proceed();
            } finally {
                stopWatch.stop();
                messageBuilder.append(new DecimalFormat("#,###").format(stopWatch.getTotalTimeMillis())).append("ms").append('\t');
            }
        } else {
            return joinPoint.proceed();
        }
    }

    @Around(value = "execution(* com.autonomy.aci.client.transport.AciHttpClient.executeAction(..)) && args(serverDetails, parameters)",
            argNames = "joinPoint,serverDetails,parameters")
    public Object logIdolRequests(
            final ProceedingJoinPoint joinPoint,
            final AciServerDetails serverDetails,
            final Collection<? extends AciParameter> parameters) throws Throwable {

        final String host = serverDetails.getHost();
        final int port = serverDetails.getPort();

        final String query = parameters.stream()
                .map(parameter -> parameter.getName() + NAME_VALUE_SEPARATOR + getParameterValue(parameter))
                .sorted()
                .collect(Collectors.joining(PARAMETER_SEPARATOR));

        final StringBuilder messageBuilder = new StringBuilder()
                .append(configService.getConfig().lookupComponentNameByHostAndPort(host, port)).append('\t')
                .append(host).append('\t')
                .append(port).append('\t')
                .append(query).append('\t');

        AciResponseInputStream aciResponseInputStream = null;
        try {
            aciResponseInputStream = (AciResponseInputStream) timeIdolRequests(joinPoint, messageBuilder);
            return aciResponseInputStream;
        } finally {
            if (aciResponseInputStream != null) {
                messageBuilder.append(aciResponseInputStream.getStatusCode() + '\t');
                messageBuilder.append(generateMessage(serverDetails, parameters));
                log.info(IDOL, messageBuilder.toString());
            }
        }
    }

    /**
     * @param serverDetails server details of idol component
     * @param parameters    parameters sent in Idol request
     * @return A custom message
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    protected String generateMessage(final AciServerDetails serverDetails, final Collection<? extends AciParameter> parameters) {
        return "";
    }

    private String getParameterValue(final AciParameter parameter) {
        return parameter.getName().equalsIgnoreCase(QueryParams.SecurityInfo.name()) || parameter.getName().equalsIgnoreCase(SecurityParams.Password.name())
                ? HIDDEN_VALUE
                : parameter.getValue();
    }
}
