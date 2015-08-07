/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.view.hod;

import org.apache.commons.lang.Validate;

/**
 * The {@code FieldTextWrapper} class makes an immutable copy of another {@code FieldText} object. Though the classes
 * representing specifiers in this package are already immutable, {@code FieldTextBuilder} is not and in general
 * {@code FieldText} implementations are not obliged to be immutable. A {@code FieldTextWrapper} instance is not backed
 * by the original object, a copy of its size and string representation are taken.
 *
 * Objects that are not {@code FieldText} instances can also be wrapped, assuming that their {@code toString()} methods
 * return a fieldtext expression.
 */
public final class FieldTextWrapper extends AbstractFieldText {
    private final int size;
    private final String fieldText;

    /**
     * Creates an immutable copy of the specified {@code FieldText} object. The copy is not backed by the original
     * object.
     *
     * @param fieldText The object to copy. Must be non-null.
     */
    public FieldTextWrapper(final FieldText fieldText) {
        Validate.notNull(fieldText, "FieldText must not be null");

        this.fieldText = fieldText.toString();
        this.size = fieldText.size();

        Validate.notNull(this.fieldText, "FieldText string must not be null");
        Validate.isTrue(size >= 0, "FieldText size must be non-negative");
    }

    /**
     * Though primarily intended for converting a {@code String} representation of fieldtext into genuine a
     * {@code FieldText} object, any object may be used with a suitable {@code toString()} implementation. A suitable
     * size will be assigned though it may not accurately reflect the actual size of the fieldtext expression.
     *
     * @param fieldText The object to wrap. Must be non-null.
     */
    public FieldTextWrapper(final Object fieldText) {
        Validate.notNull(fieldText, "FieldText must not be null");

        this.fieldText = fieldText.toString().trim();

        if (this.fieldText.length() == 0) {
            this.size = 0;
        }
        else {
            this.size = 2;
        }
    }

    /**
     * Though primarily intended for converting a {@code String} representation of fieldtext into genuine a
     * {@code FieldText} object, any object may be used with a suitable {@code toString()} implementation.
     *
     * @param fieldText The object to wrap. Must be non-null.
     * @param size The size of the {@code FieldTextWrapper}. Must be non-negative.
     */
    public FieldTextWrapper(final Object fieldText, final int size) {
        Validate.notNull(fieldText, "FieldText must not be null");
        Validate.isTrue(size >= 0, "FieldText size must not be negative");

        this.fieldText = fieldText.toString().trim();
        this.size = size;

        Validate.notNull(this.fieldText, "FieldText must not be null");
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return fieldText;
    }
}
