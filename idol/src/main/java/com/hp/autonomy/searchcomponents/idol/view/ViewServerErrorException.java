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

package com.hp.autonomy.searchcomponents.idol.view;

import lombok.Getter;

public class ViewServerErrorException extends RuntimeException {
    private static final long serialVersionUID = 6320193035187739418L;

    @Getter
    private final String reference;

    public ViewServerErrorException(final String reference, final Throwable cause) {
        super("HTTP error communicating with ViewServer", cause);

        this.reference = reference;
    }
}
