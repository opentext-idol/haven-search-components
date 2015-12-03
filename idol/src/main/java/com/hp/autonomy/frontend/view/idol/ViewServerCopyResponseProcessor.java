/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.view.idol;

import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.transport.AciResponseInputStream;
import com.hp.autonomy.frontend.view.idol.ViewServerErrorException;
import com.hp.autonomy.idolutils.processors.CopyResponseProcessor;

import java.io.OutputStream;

public class ViewServerCopyResponseProcessor implements Processor<Boolean> {
    private static final long serialVersionUID = -4149839067534521407L;

    private final CopyResponseProcessor copyResponseProcessor;
    private final String reference;

    public ViewServerCopyResponseProcessor(final OutputStream outputStream, final String reference) {
        copyResponseProcessor = new CopyResponseProcessor(outputStream);
        this.reference = reference;
    }

    @Override
    public Boolean process(final AciResponseInputStream aciResponse) {
        final int statusCode = aciResponse.getStatusCode();

        if (statusCode < 200 || statusCode > 299) {
            throw new ViewServerErrorException(statusCode, reference);
        }

        return copyResponseProcessor.process(aciResponse);
    }
}
