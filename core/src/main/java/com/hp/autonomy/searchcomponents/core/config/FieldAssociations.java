/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class FieldAssociations implements Serializable {
    private static final long serialVersionUID = -1612170214513892170L;

    private String mediaContentType;
    private String mediaUrl;
    private String mediaOffset;
    private String author;
    private String mmapUrl;
    private String thumbnail;

    public FieldAssociations merge(final FieldAssociations other) {
        if (other != null) {
            mediaContentType = mediaContentType == null ? other.mediaContentType : mediaContentType;
            mediaUrl = mediaUrl == null ? other.mediaUrl : mediaUrl;
            mediaOffset = mediaOffset == null ? other.mediaOffset : mediaOffset;
            author = author == null ? other.author : author;
            mmapUrl = mmapUrl == null ? other.mmapUrl : mmapUrl;
            thumbnail = thumbnail == null ? other.thumbnail : thumbnail;
        }

        return this;
    }
}
