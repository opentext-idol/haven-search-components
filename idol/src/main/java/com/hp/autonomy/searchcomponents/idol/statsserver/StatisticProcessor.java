/*
 * Copyright 2015-2016 Open Text.
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

package com.hp.autonomy.searchcomponents.idol.statsserver;

import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactory;
import com.autonomy.aci.client.services.ProcessorException;
import com.autonomy.aci.client.services.StAXProcessor;
import com.autonomy.aci.client.services.impl.AbstractStAXProcessor;
import com.autonomy.aci.client.services.impl.ErrorProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link AbstractStAXProcessor} to process the output of StatsServer GetStatus into an {@link Set} of
 * {@link Statistic}.
 */
@SuppressWarnings("WeakerAccess")
public class StatisticProcessor extends AbstractStAXProcessor<Set<Statistic>> {
    private static final long serialVersionUID = -5289889557164139830L;

    private final StAXProcessor<Statistic> statProcessor;

    public StatisticProcessor(final IdolAnnotationsProcessorFactory processorFactory) {
        statProcessor = processorFactory.forClass(Statistic.class);
    }

    @Override
    public Set<Statistic> process(final XMLStreamReader xmlStreamReader) {
        final Set<Statistic> statistics = new HashSet<>();

        try {
            if (isErrorResponse(xmlStreamReader)) {
                setErrorProcessor(new ErrorProcessor());
                processErrorResponse(xmlStreamReader);
            }

            while (xmlStreamReader.hasNext()) {
                if (xmlStreamReader.next() == XMLEvent.START_ELEMENT && "idol".equals(xmlStreamReader.getLocalName())) {
                    statistics.addAll(processIdol(xmlStreamReader));
                }
            }
        } catch (final XMLStreamException e) {
            throw new ProcessorException(e);
        }

        return statistics;
    }

    private Collection<Statistic> processIdol(final XMLStreamReader xmlStreamReader) throws XMLStreamException {
        final Collection<Statistic> statistics = new HashSet<>();
        String idol = null;

        while (xmlStreamReader.hasNext()) {
            final int next = xmlStreamReader.next();

            if (next == XMLEvent.START_ELEMENT) {
                final String localName = xmlStreamReader.getLocalName();

                if ("name".equals(localName)) {
                    idol = xmlStreamReader.getElementText();
                } else if ("stat".equals(localName)) {
                    statistics.add(statProcessor.process(xmlStreamReader));
                }
            } else if (next == XMLEvent.END_ELEMENT && "idol".equals(xmlStreamReader.getLocalName())) {
                if (!statistics.isEmpty() && idol == null) {
                    throw new ProcessorException("No name found for idol");
                }

                for (final Statistic statistic : statistics) {
                    statistic.setIdol(idol);
                }

                return statistics;
            }
        }

        throw new ProcessorException("No closing tag found for idol");
    }
}
