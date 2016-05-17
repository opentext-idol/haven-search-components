/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.statsserver;

import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactoryImpl;
import com.hp.autonomy.test.xml.XmlTestUtils;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNot.not;

public class StatisticProcessorTest {
    private StatisticProcessor processor;

    @Before
    public void setUp() {
        processor = new StatisticProcessor(new IdolAnnotationsProcessorFactoryImpl());
    }

    @Test
    public void processesXml() throws XMLStreamException {
        final XMLStreamReader reader = XmlTestUtils.getResourceAsXMLStreamReader("/stats-server-get-status-response.xml");
        final Set<Statistic> statistics = processor.process(reader);

        assertThat(statistics, hasSize(3));

        Statistic dynamicAgents = null;
        Statistic contentCount = null;
        Statistic contentTopN = null;

        for (final Statistic statistic : statistics) {
            if (statistic.getName().equals("DynamicAgentCount")) {
                dynamicAgents = statistic;
            } else if (statistic.getName().equals("ContentCount")) {
                contentCount = statistic;
            } else if (statistic.getName().equals("ContentTopN")) {
                contentTopN = statistic;
            }
        }

        assertThat(dynamicAgents, is(not(nullValue())));
        assertThat(dynamicAgents.getIdol(), is("Agentstore"));
        assertThat(dynamicAgents.getPeriod(), is(100L));
        assertThat(dynamicAgents.getType(), is("count"));
        assertThat(dynamicAgents.getDynamic(), is(true));

        assertThat(contentCount, is(not(nullValue())));
        assertThat(contentCount.getIdol(), is("Content"));
        assertThat(contentCount.getPeriod(), is(1000L));
        assertThat(contentCount.getType(), is("count"));
        assertThat(contentCount.getDynamic(), is(false));

        assertThat(contentTopN, is(not(nullValue())));
        assertThat(contentTopN.getIdol(), is("Content"));
        assertThat(contentTopN.getPeriod(), is(86400L));
        assertThat(contentTopN.getType(), is("topn"));
        assertThat(contentTopN.getDynamic(), is(false));
    }
}
