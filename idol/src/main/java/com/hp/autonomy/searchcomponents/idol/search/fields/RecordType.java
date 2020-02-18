/*
 * Copyright 2020 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search.fields;

import com.hp.autonomy.searchcomponents.core.config.FieldType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Helper for parsing fields with type {@link FieldType.RECORD}
 */
public final class RecordType {

    /**
     * Parse a field as a record type.
     *
     * @param node One of the field's values, as its full XML structure
     * @return Parsed field
     */
    public static Serializable parseValue(final Node node) {
        // child nodes and attributes
        final HashMap<String, List<Serializable>> resultRecord = new HashMap<>();
        // all directly contained text nodes
        final StringBuilder resultText = new StringBuilder();

        final NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node childNode = childNodes.item(i);
            if (childNode instanceof Text) {
                resultText.append(childNode.getNodeValue());
            } else {
                final String childName = childNode.getNodeName().toLowerCase();
                resultRecord.putIfAbsent(childName, new ArrayList<>());
                resultRecord.get(childName).add(parseValue(childNode));
            }
        }

        if (node.hasAttributes()) {
            final NamedNodeMap attrs = node.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
                final Node attrNode = attrs.item(i);
                final String attrName = "@" + attrNode.getNodeName().toLowerCase();
                // attributes are lists too, for consistency
                resultRecord.putIfAbsent(attrName, new ArrayList<>());
                // of course an attribute always a text node
                resultRecord.get(attrName).add(attrNode.getNodeValue());
            }
        }

        // note: we drop any text if there are child nodes or attributes
        return !resultRecord.isEmpty() ? resultRecord : resultText.toString();
    }

}
