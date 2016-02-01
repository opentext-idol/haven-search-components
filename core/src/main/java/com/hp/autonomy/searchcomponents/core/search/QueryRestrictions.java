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

    String getLanguageType();

    List<String> getStateMatchId();

    List<String> getStateDontMatchId();

    boolean isAnyLanguage();
}
