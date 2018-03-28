/*
 * Copyright 2018 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.answer.requests;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@SuppressWarnings("unused")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", defaultImpl = ConversationRequestImpl.class)
@JsonSubTypes(@JsonSubTypes.Type(ConversationRequestImpl.class))
public interface ConversationRequestMixin {
}
