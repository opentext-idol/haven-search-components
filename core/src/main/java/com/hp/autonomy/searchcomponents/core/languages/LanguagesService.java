/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.languages;

import com.hp.autonomy.types.idol.LanguageType;

import java.util.Map;

public interface LanguagesService {

    Map<String, LanguageType> getLanguages();

    String getDefaultLanguageId();

    boolean isValidLanguage(final String language);

}
