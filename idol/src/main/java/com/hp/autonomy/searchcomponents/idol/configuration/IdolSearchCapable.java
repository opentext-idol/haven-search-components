/*
 * Copyright 2015-2018 Open Text.
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

package com.hp.autonomy.searchcomponents.idol.configuration;

import com.autonomy.aci.client.transport.AciServerDetails;
import com.hp.autonomy.searchcomponents.core.config.HavenSearchCapable;
import com.hp.autonomy.searchcomponents.idol.answer.configuration.AnswerServerConfig;
import com.hp.autonomy.searchcomponents.idol.view.configuration.ViewCapable;
import com.hp.autonomy.types.requests.idol.actions.query.params.CombineParam;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Configuration required for any application performing Haven Search
 */
public interface IdolSearchCapable extends HavenSearchCapable, ViewCapable, IdolComponentLabelLookup {
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

    /**
     * Returns the primary reference field which should be used for getContent, in case there's multiple references;
     * can be null.
     * @return String reference field, or null if not set.
     */
    String getReferenceField();

    /**
     * Returns the combine method for combining results when doing queries.
     * @return String the combine method
     */
    default String getCombineMethod() {
        return CombineParam.Simple.name();
    };

    String getStoredStateField();

}
