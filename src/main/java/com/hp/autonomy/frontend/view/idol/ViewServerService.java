package com.hp.autonomy.frontend.view.idol;

import com.autonomy.aci.client.services.Processor;

public interface ViewServerService {

    <T> T viewDocument(String documentReference, Processor<T> processor) throws ViewDocumentNotFoundException, ViewNoReferenceFieldException, ReferenceFieldBlankException;
}
