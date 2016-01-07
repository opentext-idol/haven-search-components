/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.view;

public class ReferenceFieldBlankException extends RuntimeException {
    private static final long serialVersionUID = 832094076730496755L;

    public ReferenceFieldBlankException() {
        super("Your ViewServer settings are not correctly configured.");
    }

}