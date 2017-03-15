/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.configuration;

import com.autonomy.aci.client.transport.AciServerDetails;
import com.hp.autonomy.searchcomponents.core.config.HavenSearchCapable;
import com.hp.autonomy.searchcomponents.idol.answer.configuration.AnswerServerConfig;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewCapable;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Configuration required for any application performing Haven Search
 */
public interface IdolSearchCapable extends HavenSearchCapable, ViewCapable {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String QUERY_MANIPULATION_VALIDATOR_BEAN_NAME = "queryManipulationValidator";

    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String ANSWER_SERVER_VALIDATOR_BEAN_NAME = "answerServerConfigValidator";

    /**
     * Gets the name for an Idol component based upon its host name and port
     *
     * @param host component host name
     * @param port component port
     * @return friendly component name
     */
    String lookupComponentNameByHostAndPort(final String host, final int port);

    /**
     * Returns details of Idol Content engine
     *
     * @return Content engine configuration
     */
    AciServerDetails getContentAciServerDetails();

    /**
     * Returns details of Idol Query Manipulation Server
     *
     * @return QMS configuration
     */
    QueryManipulation getQueryManipulation();

    /**
     * Returns details of Answer Server
     *
     * @return AnswerServer configuration
     */
    AnswerServerConfig getAnswerServer();
}
