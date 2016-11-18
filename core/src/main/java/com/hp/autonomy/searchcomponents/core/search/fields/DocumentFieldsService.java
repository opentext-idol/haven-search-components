/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search.fields;

import com.hp.autonomy.searchcomponents.core.config.FieldInfo;
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
}
