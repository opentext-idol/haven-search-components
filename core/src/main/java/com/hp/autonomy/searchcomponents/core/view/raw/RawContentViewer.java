package com.hp.autonomy.searchcomponents.core.view.raw;

import java.io.InputStream;

/**
 * Component for rendering a document for viewing if it does not satisfy the requirements for viewing via View Server (or
 * View Server is disabled).
 */
@FunctionalInterface
public interface RawContentViewer {

    String RAW_CONTENT_VIEWER_BEAN_NAME = "rawContentViewer";

    /**
     * Create an InputStream from the document's fields suitable for streaming back to a client.
     * @param rawDocument The document's fields
     * @return An InputStream for retrieving the output
     */
    InputStream formatRawContent(RawDocument rawDocument);

}
