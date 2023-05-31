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

package com.hp.autonomy.searchcomponents.idol.exceptions;

import java.io.Serializable;

/**
 * Any known Idol error code.
 */
public interface IdolErrorCode<T extends Enum<T>> extends Serializable {
    /**
     * The known error id
     *
     * @return The known error id
     */
    String getErrorId();

    /**
     * The associated Java enum
     *
     * @return The associated Java enum
     */
    T getEnum();
}
