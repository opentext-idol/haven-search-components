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

package com.hp.autonomy.searchcomponents.core.languages;

import com.hp.autonomy.types.idol.responses.LanguageType;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;

/**
 * Service for retrieving information about the languages configured on the platform
 */
public interface LanguagesService {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String LANGUAGES_SERVICE_BEAN_NAME = "languagesService";

    /**
     * Retrieve all languages configured on the platform (utf8 only)
     *
     * @return a map of language id to language information
     */
    Map<String, LanguageType> getLanguages();

    /**
     * Returns the id of the default language on the platfrom
     *
     * @return the id of the default language on the platfrom
     */
    String getDefaultLanguageId();

    /**
     * Verifies whether the supplied language id is valid
     *
     * @param language a language id
     * @return whether or not the language id is valid
     */
    boolean isValidLanguage(final String language);
}
