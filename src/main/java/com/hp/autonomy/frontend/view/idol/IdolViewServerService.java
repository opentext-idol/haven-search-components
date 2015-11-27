package com.hp.autonomy.frontend.view.idol;

import com.autonomy.aci.actions.idol.query.Document;
import com.autonomy.aci.actions.idol.query.DocumentField;
import com.autonomy.aci.actions.idol.query.QueryResponse;
import com.autonomy.aci.actions.idol.query.QueryResponseProcessor;
import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.autonomy.aci.content.identifier.reference.Reference;
import com.autonomy.aci.content.printfields.PrintFields;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.view.idol.configuration.ViewCapable;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

public class IdolViewServerService implements ViewServerService {
    private final AciService contentAciService;
    private final AciService viewAciService;
    private final ConfigService<? extends ViewCapable> configService;

    public IdolViewServerService(final AciService contentAciService, final AciService viewAciService, final ConfigService<? extends ViewCapable> configService) {
        this.contentAciService = contentAciService;
        this.viewAciService = viewAciService;
        this.configService = configService;
    }

    /**
     * Provides an HTML rendering of the given IDOL document reference. This first performs a GetContent to make sure the
     * document exists, then reads the configured reference field and passes the value of the field to ViewServer.
     *
     * @param documentReference The IDOL document reference of the document to view
     * @param processor         The processor that will consume the ViewServer output
     * @param <T>               The return type of the processor
     * @return The return value of the given processor
     * @throws ViewDocumentNotFoundException If the given document reference does not exist in IDOL
     * @throws ViewNoReferenceFieldException If the document with the given reference does not have the required reference field
     * @throws ReferenceFieldBlankException  If the configured reference field name is blank
     * @throws ViewServerErrorException      If ViewServer returns a status code outside the 200 range
     */
    @Override
    public <T> T viewDocument(final String documentReference, final Processor<T> processor) throws ViewDocumentNotFoundException, ViewNoReferenceFieldException, ReferenceFieldBlankException {
        final String referenceField = configService.getConfig().getViewConfig().getReferenceField();
        if (StringUtils.isEmpty(referenceField)) {
            throw new ReferenceFieldBlankException();
        }

        final AciParameters parameters = new AciParameters("GetContent");
        parameters.add("Reference", new Reference(documentReference));
        parameters.add("Print", "Fields");
        parameters.add("PrintFields", new PrintFields(referenceField));

        final QueryResponse queryResponse;
        try {
            queryResponse = contentAciService.executeAction(parameters, new QueryResponseProcessor());
        } catch (final AciErrorException e) {
            throw new ViewDocumentNotFoundException(documentReference);
        }

        final List<Document> documents = queryResponse.getDocuments();
        if (documents.isEmpty()) {
            throw new ViewDocumentNotFoundException(documentReference);
        }

        final Document document = documents.get(0);
        String referenceFieldValue = null;

        // Assume the field names are case insensitive
        for (final Map.Entry<String, List<DocumentField>> entry : document.getDocumentFields().entrySet()) {
            if (entry.getKey().equalsIgnoreCase(referenceField)) {
                referenceFieldValue = entry.getValue().get(0).getValue();
                break;
            }
        }

        if (referenceFieldValue == null) {
            throw new ViewNoReferenceFieldException(documentReference, referenceField);
        }

        final AciParameters viewParameters = new AciParameters("View");
        viewParameters.add("NoACI", true);
        viewParameters.add("Reference", referenceFieldValue);
        viewParameters.add("EmbedImages", true);
        viewParameters.add("StripScript", true);

        return viewAciService.executeAction(viewParameters, processor);
    }

}
