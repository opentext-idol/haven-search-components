/*
 * Copyright 2018 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.config;

import java.io.Serializable;
import java.util.LinkedHashMap;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The different possible ways you could convert an IDOL DOM element into a map
 */
public enum MapType {
    ATTRIBUTE,
    ELEMENTNAME;

    public LinkedHashMap<String, Serializable> parseMapType(final FieldType fieldType, final Node node) {
        final LinkedHashMap<String, Serializable> map = new LinkedHashMap<>();

        switch(this) {
            case ELEMENTNAME:
                final NodeList children = node.getChildNodes();
                for (int jj = 0, max = children.getLength(); jj < max; ++jj) {
                    final Node child = children.item(jj);

                    if (child instanceof Element) {
                        final Element el = (Element) child;
                        map.put(el.getTagName(), (Serializable) fieldType.parseValue(fieldType.getType(), el.getTextContent()));
                    }
                }

                return map;
            case ATTRIBUTE:
                final NamedNodeMap attributes = node.getAttributes();
                if (attributes != null) {
                    for(int jj = 0, max = attributes.getLength(); jj < max; ++jj) {
                        final Node attrib = attributes.item(jj);
                        map.put(attrib.getNodeName(), (Serializable) fieldType.parseValue(fieldType.getType(), attrib.getNodeValue()));
                    }
                }
                return map;
            default:
                throw new UnsupportedOperationException("Unable to parse an unknown map type");
        }
    }
}
