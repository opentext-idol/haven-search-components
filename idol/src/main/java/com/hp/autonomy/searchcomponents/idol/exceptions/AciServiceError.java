/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.exceptions;

import java.io.Serializable;

/**
 * An error which may be thrown in a service-level class which communicates with Idol
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@FunctionalInterface
public interface AciServiceError<T extends Enum<T>> extends Serializable {
    T getEnum();
}
