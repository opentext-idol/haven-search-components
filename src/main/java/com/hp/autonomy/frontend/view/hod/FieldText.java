/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */
package com.hp.autonomy.frontend.view.hod;

/**
 * Interface for representing IDOL FieldText expressions. These expressions are made up of specifiers combined with AND,
 * OR, XOR, NOT and brackets. e.g.
 *
 * <pre>
 *    MATCH{xls,doc}:EXT+AND+EQUAL{4}:TAG
 * </pre>
 *
 * The FieldTextBuilder class can be used to combine FieldText specifiers into more complex expressions.
 * However, it is often unnecessary to explicitly instantiate a {@link FieldTextBuilder} as the {@code FieldText}
 * interface forces all specifiers to provide support for combining expressions.
 * <p/>
 * The {@link Specifier} class provides both a generic object for representing a specifier as well as being a suitable
 * base class for other specifiers.
 * <p/>
 * Example:
 *
 * <pre>
 *    FieldText ft = new MATCH("EXT", "xls", "doc").AND(new EQUAL("TAG", 4));
 *    String fieldText = ft.toString();
 * </pre>
 *
 * @author darrelln
 * @author boba
 * @author williamo
 * @version $Revision: #3 $ $Date: 2014/12/10 $
 */
interface FieldText {

    /**
     * Combines two FieldText expressions ({@code this} and {@code fieldText}) using the AND operator. This should
     * return a {@code FieldText} object equivalent to:
     *
     * <pre>(this) AND (fieldText)</pre>
     *
     * Calling this method might alter the {@code FieldText} object on which it is called.
     *
     * @param fieldText A fieldtext expression or specifier.
     * @return The combined expression.
     */
    FieldText AND(FieldText fieldText);

    /**
     * Combines two FieldText expressions ({@code this} and {@code fieldText}) using the OR operator. This should return
     * a {@code FieldText} object equivalent to:
     *
     * <pre>(this) OR (fieldText)</pre>
     *
     * Calling this method might alter the {@code FieldText} object on which it is called.
     *
     * @param fieldText A fieldtext expression or specifier.
     * @return The combined expression.
     */
    FieldText OR(FieldText fieldText);

    /**
     * Applies a NOT operator to this FieldText expression. This should return a {@code FieldText} object equivalent to:
     *
     * <pre>NOT (this)</pre>
     *
     * Calling this method might alter the {@code FieldText} object on which it is called.
     *
     * @return The negated expression.
     */
    FieldText NOT();

    /**
     * Combines two FieldText expressions ({@code this} and {@code fieldText}) using the XOR operator. This should
     * return a {@code FieldText} object equivalent to:
     *
     * <pre>(this) XOR (fieldText)</pre>
     *
     * Calling this method might alter the {@code FieldText} object on which it is called.
     *
     * @param fieldText A fieldtext expression or specifier.
     * @return The combined expression.
     */
    FieldText XOR(FieldText fieldText);

    // Currently IDOL doesn't support chaining BEFOREs/AFTERs, which makes them tricky to implement well. Until someone
    // requests them we'll leave them out for now
    //
    // FieldText BEFORE(FieldText fieldText);
    // FieldText AFTER(FieldText fieldText);

    /*
     * Combines two FieldText specifiers ({@code this} and {@code fieldText}) using the WHEN operator. Both specifiers
     * must have a size of 1 or an exception will be thrown. The returned {@code FieldText} object will be equivalent
     * to:
     *
     * <pre>this WHEN fieldText</pre>
     *
     * Calling this method might alter the {@code FieldText} object on which it is called.
     *
     * @param fieldText A fieldtext expression or specifier.
     * @return The combined expression.
     * @throws IllegalStateException If {@code this} does not have a size of 1
     * @throws IllegalArgumentException If {@code fieldText} does not have a size of 1
     */
    FieldText WHEN(FieldText fieldText);

    /*
     * Combines two FieldText expressions (<tt>this</tt> and <tt>fieldText</tt>)
     * using the WHEN<i>n</i> operator. This should return a <tt>FieldText</tt> object
     * equivalent to:
     *
     * <pre>(this) WHEN<i>n</i> (fieldText)</pre>
     *
     * Calling this method might alter the <tt>FieldText</tt> object on which it
     * is called.
     *
     * @param depth The <i>n</i> in WHEN<i>n</i>
     * @param fieldText A fieldtext expression or specifier.
     * @return The combined expression.
     * @throws IllegalStateException If {@code this} does not have a size of 1
     * @throws IllegalArgumentException If {@code fieldText} does not have a size of 1
     */
    FieldText WHEN(int depth, FieldText fieldText);

    /**
     * The size is defined as the number of specifiers in the FieldText expression. An individual specifier would have a
     * size of 1.
     *
     * @return The size of the expression.
     */
    int size();

    /**
     * A {@code FieldText} object is empty if its size is zero.
     *
     * @return {@code true} if and only if size is 0.
     */
    boolean isEmpty();

    /**
     * Should return the {@code String} representation of the FieldText expression, exactly as it should be sent to
     * IDOL.
     *
     * @return The {@code String} representation.
     */
    @Override
    String toString();

    /**
     * {@code FieldText} objects are considered equal if their {@code String} representations are equal.
     *
     * @param obj An object to test for equality.
     * @return {@code true} if and only if {@code obj} is a {@code FieldText} object with the same {@code toString()}
     *         value as this object.
     */
    @Override
    boolean equals(Object obj);

    /**
     * The hashcode should be that of the {@code String} representation.
     *
     * @return The hashcode of the {@code String} representation.
     */
    @Override
    int hashCode();
}
