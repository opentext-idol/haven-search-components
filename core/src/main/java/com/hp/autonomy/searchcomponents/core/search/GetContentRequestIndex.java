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

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.requests.RequestObject;

import java.io.Serializable;
import java.util.Set;

/**
 * Option for interacting with {@link DocumentsService#getDocumentContent(GetContentRequest)}
 *
 * @param <S> The type of the database identifier
 */
public interface GetContentRequestIndex<S extends Serializable> extends RequestObject<GetContentRequestIndex<S>, GetContentRequestIndexBuilder<?, S, ?>> {
    /**
     * The database in which the references are located
     *
     * @return The database in which the references are located
     */
    S getIndex();

    /**
     * The references of the documents whose content is to be queried
     *
     * @return The references of the documents whose content is to be queried
     */
    Set<String> getReferences();
}
