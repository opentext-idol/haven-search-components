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

package com.hp.autonomy.searchcomponents.idol.view;

import lombok.Getter;

public class ViewDocumentNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 2505063806464536375L;
    @Getter
    private final String reference;

    public ViewDocumentNotFoundException(final String reference) {
        this(reference, null);
    }

    public ViewDocumentNotFoundException(final String reference, final Exception cause) {
        super("The document " + reference + " could not be found", cause);
        this.reference = reference;
    }
}
