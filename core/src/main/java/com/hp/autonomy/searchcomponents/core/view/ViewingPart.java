package com.hp.autonomy.searchcomponents.core.view;

/**
 * How to view a document.
 */
public enum ViewingPart {
    /**
     * View whole document as HTML.
     */
    DOCUMENT,
    /**
     * View part of the document in HTML - requested by a=getlink requests embedded in the DOCUMENT render.
     */
    SUBDOCUMENT,
    /**
     * Get original document file in original format.
     */
    ORIGINAL;
}
