/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

public interface QueryRestrictions<S extends Serializable> extends Serializable {
    String getQueryText();

    String getFieldText();

    List<S> getDatabases();

    DateTime getMinDate();

    DateTime getMaxDate();

    Integer getMinScore();

    String getLanguageType();

    List<String> getStateMatchId();

    List<String> getStateDontMatchId();

    boolean isAnyLanguage();

    interface Builder<Q extends QueryRestrictions<S>, S extends Serializable> {
        Builder<Q, S> setQueryText(String queryText);

        Builder<Q, S> setFieldText(String fieldText);

        Builder<Q, S> setDatabases(List<S> databases);

        Builder<Q, S> setMinDate(DateTime minDate);

        Builder<Q, S> setMaxDate(DateTime maxDate);

        Builder<Q, S> setMinScore(Integer minScore);

        Builder<Q, S> setStateMatchId(List<String> stateMatchId);

        Builder<Q, S> setStateDontMatchId(List<String> stateDontMatchId);

        Q build();
    }
}
