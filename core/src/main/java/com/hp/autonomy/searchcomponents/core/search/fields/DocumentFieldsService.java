/*
 * Copyright 2015 Open Text.
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

package com.hp.autonomy.searchcomponents.core.search.fields;

import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collection;
import java.util.List;

/**
 * Uses configuration to determine what fields should be returned in a query response
 */
public interface DocumentFieldsService {
    /**
     * The bean name of the default implementation.
     * Use this in an {@link Qualifier} tag to access this implementation via autowiring.
     */
    String DOCUMENT_FIELDS_SERVICE_BEAN_NAME = "documentFieldsService";

    /**
     *  Get fields for the printFields query parameter. Returns hard-coded fields and those in the config file,
     *  restricted by printFields parameter if it is non-empty.
     * @param printFields Ids of user-selected fields
     * @return Ids of fields to include in printFields query parameter
     */
    List<String> getPrintFields(Collection<String> printFields);

    /**
     * @return Additional fields toi read from the result, e.g. related to query manipulation.
     */
    Collection<FieldInfo<?>> getHardCodedFields();

    /**
     * @return get fields which are tagged as 'editable'
     */
    Set<String> getEditableIdolFields(String field);
}
