/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
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
