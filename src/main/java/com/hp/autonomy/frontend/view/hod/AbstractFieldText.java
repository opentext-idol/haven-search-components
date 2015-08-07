/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */
package com.hp.autonomy.frontend.view.hod;

/**
 * An abstract base-class that partially implements the {@link FieldText} interface using sensible defaults. Under
 * specific circumstances, alternatives exist to subclassing {@code AbstractFieldText} directly:
 *
 * <ul>
 *   <li>Subclassing {@link Specifier} for an individual fieldtext specifier.
 *   <li>Making an immutable singleton, wrapped by {@link FieldTextWrapper}, if the expression is a constant.
 * </ul>
 *
 * @author darrelln
 * @author boba
 * @author williamo
 * @version $Revision: #4 $ $Date: 2014/12/10 $
 */
public abstract class AbstractFieldText implements FieldText {

    /**
     * Appends another fieldtext expression onto the end of this expression using the AND operator. The returned
     * {@link FieldText} object is equivalent to:
     *
     * <pre>(this) AND (fieldText)</pre>
     *
     * @param fieldText A fieldtext expression or specifier.
     * @return The combined expression.
     */
    @Override
    public FieldText AND(final FieldText fieldText) {
        return new FieldTextBuilder(this).AND(fieldText);
    }

    /**
     * Appends another fieldtext expression onto the end of this expression using the OR operator. The returned
     * {@link FieldText} object is equivalent to:
     *
     * <pre>(this) OR (fieldText)</pre>
     *
     * @param fieldText A fieldtext expression or specifier.
     * @return The combined expression.
     */
    @Override
    public FieldText OR(final FieldText fieldText) {
        return new FieldTextBuilder(this).OR(fieldText);
    }

    /**
     * Applies a NOT operator to this fieldtext. The returned {@link FieldText} object is equivalent to:
     *
     * <pre>NOT (this)</pre>
     *
     * @return The negated fieldtext.
     */
    @Override
    public FieldText NOT() {
        return new FieldTextBuilder(this).NOT();
    }

    /**
     * Appends another fieldtext expression onto the end of this expression using the XOR operator. The returned
     * {@link FieldText} object is equivalent to:
     *
     * <pre>(this) XOR (fieldText)</pre>
     *
     * @param fieldText A fieldtext expression or specifier.
     * @return The combined expression.
     */
    @Override
    public FieldText XOR(final FieldText fieldText) {
        return new FieldTextBuilder(this).XOR(fieldText);
    }

    /**
     * Appends another fieldtext expression onto the end of this expression using the WHEN operator. The returned
     * {@link FieldText} object is equivalent to:
     *
     * <pre>(this) WHEN (fieldText)</pre>
     *
     * @param fieldText A fieldtext expression or specifier.
     * @return The combined expression.
     */
    @Override
    public FieldText WHEN(final FieldText fieldText) {
        return new FieldTextBuilder(this).WHEN(fieldText);
    }

    /**
     * Appends another fieldtext expression onto the end of this expression using the WHEN<i>n</i> operator. The
     * returned {@link FieldText} object is equivalent to:
     *
     * <pre>(this) WHEN<i>n</i> (fieldText)</pre>
     *
     * @param depth The <i>n</i> in WHEN<i>n</i>
     * @param fieldText A fieldtext expression or specifier.
     * @return The combined expression.
     */
    @Override
    public FieldText WHEN(final int depth, final FieldText fieldText) {
        return new FieldTextBuilder(this).WHEN(depth, fieldText);
    }

    /**
     * Simple implementation that tests for emptiness using {@code size() == 0}. In most cases this should be
     * overridden with a better implementation.
     *
     * @return {@code true} if and only if size is {@code 0}.
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Tests for equality of this object with another {@link FieldText} object.
     *
     * @param obj An object to test for equality.
     * @return {@code true} if and only if <tt>obj</tt> is a {@link FieldText} object with the same <tt>toString()</tt>
     * value as this object.
     */
    @Override
    public boolean equals(final Object obj) {
        return this == obj || obj instanceof FieldText && toString().equals(obj.toString());
    }

    /**
     * A suitable hash code based on the toString() method.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Should return the {@code String} representation of the FieldText expression, exactly as it should be sent to
     * IDOL.
     *
     * @return The {@code String} representation.
     */
    @Override
    public abstract String toString();

}
