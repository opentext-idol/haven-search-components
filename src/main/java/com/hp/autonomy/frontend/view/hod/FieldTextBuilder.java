/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */
package com.hp.autonomy.frontend.view.hod;

import org.apache.commons.lang.Validate;

import static com.hp.autonomy.frontend.view.hod.FieldTexts.MATCHNOTHING;

/**
 * <p>{@code FieldTextBuilder} allows for the building of complex fieldtext expressions from other fieldtext expressions
 * or specifiers. e.g.:
 *
 * <pre>
 *    FieldTextBuilder builder = new FieldTextBuilder(new MATCH("MYFIELD", "VALUE"));
 *    builder.AND(new EQUAL("NUMERIC_FIELD", 6));</pre>
 *
 * evaluates to:
 *
 * <pre>    MATCH{VALUE}:MYFIELD+AND+EQUAL{6}:NUMERIC_FIELD</pre>
 *
 * Note that calls to {@code AND}, {@code OR}, {@code XOR} and {@code NOT} return the same builder on which they were
 * called so the return value is usually discarded.
 *
 * <p>{@code FieldTextBuilder} manages boolean operators to ensure that there are no leading or trailing operators.
 * Further, it will omit parentheses where possible.
 *
 * <p>This class is {@code final} to ensure that the assumptions made in the optimizations for combining
 * {@code FieldTextBuilder}s cannot be violated.
 *
 * <p>This class makes no attempt at internal synchronization and is not safe to be used by multiple threads without
 * external synchronization.
 *
 * <p>The lack of an empty constructor is not an oversight, it caused some confusion in earlier versions of the class
 * and has been removed. The problem occurred in the treatment of the OR operator, which is logically expected to
 * increase the size of the results set. However, an empty fieldtext matches all documents so appending more fieldtext
 * via OR should have no effect. This was not the behaviour in earlier versions of this class and this counter-intuitive
 * behaviour led to problems. The problem was fixed using the FieldTexts#MATCHNOTHING specifier but introduced
 * another problem in that it was now much more difficult to build up a sequence of expressions separated by OR. Static
 * factory methods have been added to make thid easier, see the examples below for current usage patterns.
 *
 * <p>Example 1 - Some of the fieldtext is fixed and can be added at construction time:
 *
 * <pre>
 *    FieldTextBuilder builder = new FieldTextBuilder(new MATCH("FIELD", "VALUE"));</pre>
 *
 * <p>or:
 *
 * <pre>
 *    FieldTextBuilder builder = FieldTextBuilder.from(new MATCH("FIELD", "VALUE"));</pre>
 *
 * <p>Example 2 - Creating a sequence of ORs with multiple expressions (likewise for AND and XOR):
 *
 * <pre>
 *    FieldTextBuilder builder = FieldTexts.OR(
 *        new MATCH("FIELD", "VALUE"),
 *        new EQUAL("NUMERIC", 7, 8),
 *        new EXISTS("UUID")
 *    );</pre>
 *
 * <p>Example 3 - Conditionally building ORs when none of the fieldtext is fixed (likewise for AND and XOR):
 *
 * <pre>
 *    // Create a builder suitable for appending via OR
 *    FieldTextBuilder builder = FieldTexts.OR();
 *
 *    for (fieldText : fieldTextList) {
          builder.OR(fieldText);
 *    }
 *
 *    if (builder.isMatchNothing()) {
 *        // Nothing was added as part of the loop so the fieldtext won't match any documents
 *    }
 * </pre>
 *
 * @author darrelln
 * @author boba
 * @author williamo
 * @version $Revision: #5 $ $Date: 2014/12/10 $
 */
public final class FieldTextBuilder extends AbstractFieldText {

    private final StringBuilder fieldTextString = new StringBuilder();

    private int componentCount;

    private String lastOperator; // Values: null (no last operator), "" (unknown last operator), "AND", "OR", "XOR", "WHEN", "WHENn"

    private boolean not;

    // Creating an empty FieldTextBuilder leads to confusing behaviour under boolean operators. Given this class is
    // final there's no need to worry about subclasses, so the static factory methods can be used instead to create
    // 'empty' builders
    FieldTextBuilder() {
    }

    /**
     * Creates a {@code FieldTextBuilder} containing the specified fieldtext. The new object will not be backed by the
     * {@code FieldText} provided.
     *
     * @param fieldText The initial contents of the builder.
     */
    public FieldTextBuilder(final FieldText fieldText) {
        Validate.notNull(fieldText, "FieldText should not be null");

        setFieldText(fieldText);
    }

    /**
     * Appends the specified fieldtext onto the builder using the AND operator. Parentheses are omitted where possible
     * and logical simplifications are made in certain cases:
     *
     * <pre>(A AND A) => A</pre>
     * <pre>(* AND A) => A</pre>
     * <pre>(0 AND A) => 0</pre>
     *
     * @param fieldText A fieldtext expression or specifier.
     * @return {@code this}
     */
    @Override
    public FieldTextBuilder AND(final FieldText fieldText) {
        Validate.notNull(fieldText, "FieldText should not be null");

        if (fieldText == this || toString().equals(fieldText.toString())) {
            return this;
        }

        if (isEmpty()) {
            return setFieldText(fieldText);
        }

        if (fieldText.isEmpty()) {
            return this;
        }

        if (MATCHNOTHING.equals(this) || MATCHNOTHING.equals(fieldText)) {
            return setFieldText(MATCHNOTHING);
        }

        return binaryOperation("AND", fieldText);
    }

    /**
     * Appends the specified fieldtext onto the builder using the OR operator. Parentheses are omitted where possible
     * and logical simplifications are made in certain cases:
     *
     * <pre>(A OR A) => A</pre>
     * <pre>(* OR A) => *</pre>
     * <pre>(0 OR A) => A</pre>
     *
     * @param fieldText A fieldtext expression or specifier.
     * @return {@code this}
     */
    @Override
    public FieldTextBuilder OR(final FieldText fieldText) {
        Validate.notNull(fieldText, "FieldText should not be null");

        if (fieldText == this || toString().equals(fieldText.toString())) {
            return this;
        }

        if (isEmpty() || MATCHNOTHING.equals(fieldText)) {
            return this;
        }

        if (fieldText.isEmpty() || MATCHNOTHING.equals(this)) {
            return setFieldText(fieldText);
        }

        return binaryOperation("OR", fieldText);
    }

    /**
     * Appends the specified fieldtext onto the builder using the XOR operator. Parentheses are omitted where possible
     * and logical simplifications are made in certain cases:
     *
     * <pre>(A XOR A) => 0</pre>
     * <pre>(* XOR A) => NOT A</pre>
     * <pre>(0 XOR A) => A</pre>
     *
     * @param fieldText A fieldtext expression or specifier.
     * @return {@code this}.
     */
    @Override
    public FieldTextBuilder XOR(final FieldText fieldText) {
        Validate.notNull(fieldText, "FieldText should not be null");

        if (fieldText == this || toString().equals(fieldText.toString())) {
            return setFieldText(MATCHNOTHING);
        }

        if (isEmpty()) {
            return setFieldText(fieldText).NOT();
        }

        if (fieldText.isEmpty()) {
            return NOT();
        }

        if (MATCHNOTHING.equals(this)) {
            return setFieldText(fieldText);
        }

        if (MATCHNOTHING.equals(fieldText)) {
            return this;
        }

        return binaryOperation("XOR", fieldText);
    }

    /**
     * Appends the specified fieldtext onto the builder using the WHEN operator. A simplification is made in the case
     * where the passed {@code fieldText} is equal to {@code this}:
     *
     * <pre>(A WHEN A) => A</pre>
     *
     * @param fieldText A fieldtext expression or specifier.
     * @return {@code this}
     */
    @Override
    public FieldTextBuilder WHEN(final FieldText fieldText) {
        Validate.notNull(fieldText, "FieldText should not be null");
        Validate.isTrue(fieldText.size() >= 1, "FieldText must have a size greater or equal to 1");

        if(size() < 1) {
            throw new IllegalStateException("Size must be greater or equal to 1");
        }

        // Here we assume that A WHEN A => A but is there an edge case where this doesn't work?
        if(fieldText == this || toString().equals(fieldText.toString())) {
            return this;
        }

        if(MATCHNOTHING.equals(this) || MATCHNOTHING.equals(fieldText)) {
            return setFieldText(MATCHNOTHING);
        }

        return binaryOperation("WHEN", fieldText);
    }

    /**
     * Appends the specified fieldtext onto the builder using the WHEN<i>n</i> operator.
     *
     * @param depth The <i>n</i> in WHEN<i>n</i>
     * @param fieldText A fieldtext expression or specifier.
     * @return {@code this}.
     */
    @Override
    public FieldTextBuilder WHEN(final int depth, final FieldText fieldText) {
        Validate.isTrue(depth >= 1, "Depth must be at least 1");
        Validate.notNull(fieldText, "FieldText should not be null");

        Validate.isTrue(fieldText.size() >= 1, "FieldText must have a size greater or equal to 1");

        if(size() < 1) {
            throw new IllegalStateException("Size must be greater or equal to 1");
        }

        // We omit this 'optimization' because for large n it doesn't work, X WHENn A => 0, even if X = A
        //if (fieldText == this || toString().equals(fieldText.toString())) {
        //    return this;
        //}
        if(fieldText == this) {
            return binaryOperation("WHEN" + depth, new FieldTextWrapper(this));
        }

        if(MATCHNOTHING.equals(this) || MATCHNOTHING.equals(fieldText)) {
            return setFieldText(MATCHNOTHING);
        }

        return binaryOperation("WHEN" + depth, fieldText);
    }

    /**
     * Clears any existing fieldtext and replaces it with the specified fieldtext.
     *
     * @param fieldText
     * @return
     */
    private FieldTextBuilder setFieldText(final FieldText fieldText) {
        return clear().binaryOperation(null, fieldText);
    }

    /**
     * Does the work for the OR, AND, XOR and WHEN methods.
     *
     * @param operator
     * @param fieldText
     * @return
     */
    private FieldTextBuilder binaryOperation(final String operator, final FieldText fieldText) {
        // This should never happen as the AND, OR, XOR and WHEN methods should already have checked this
        Validate.isTrue(fieldText != this);

        // Optimized case when fieldText is a FieldTextBuilder
        if (fieldText instanceof FieldTextBuilder) {
            return binaryOp(operator, (FieldTextBuilder)fieldText);
        }

        // Special case when we're empty
        if (componentCount == 0) {
            fieldTextString.append(fieldText.toString());

            if (fieldText.size() > 1) {
                lastOperator = "";
            }
        }
        else {
            // Add the NOTs, parentheses and operator
            addOperator(operator);

            // Size 1 means a single specifier, so no parentheses
            if (fieldText.size() == 1) {
                fieldTextString.append(fieldText.toString());
            }
            else {
                fieldTextString.append('(').append(fieldText.toString()).append(')');
            }
        }

        componentCount += fieldText.size();
        not = false;

        return this;
    }

    /**
     * Does the work for the OR, AND and XOR methods in the optimized case where fieldText is a FieldTextBuilder.
     *
     * @param operator
     * @param fieldText
     * @return
     */
    private FieldTextBuilder binaryOp(final String operator, final FieldTextBuilder fieldText) {
        // Special case when we're empty
        if (componentCount == 0) {
            // Just copy the argument
            fieldTextString.append(fieldText.fieldTextString);
            lastOperator = fieldText.lastOperator;
            not = fieldText.not;
            componentCount = fieldText.componentCount;
        }
        else {
            // Add the NOTs, parentheses and operator
            addOperator(operator);

            if (fieldText.lastOperator == null || fieldText.lastOperator.equals(operator) || fieldText.not) {
                fieldTextString.append(fieldText.toString());
            }
            else {
                fieldTextString.append('(').append(fieldText.toString()).append(')');
            }

            componentCount += fieldText.size();
            not = false;
        }

        return this;
    }

    /**
     * The first section of the fieldtext manipulation process, where the NOTs are handled and the operator is added,
     * are common to both binaryOp methods so this method saves us repeating the code.
     *
     * @param operator
     */
    private void addOperator(final String operator) {
        // This should never happen but sanity check...
        Validate.notNull(operator);

        if (lastOperator == null) {
            lastOperator = operator;
        }

        /* Logic to determine whether to put parentheses around the existing fieldtext. This is tied to the use of the
         * NOT operator as it also adds parentheses.
         */
        if (not) {
            if (componentCount == 1) {
                fieldTextString.insert(0, "NOT+");
            }
            else {
                fieldTextString.insert(0, "NOT(").append(')');
            }
        }
        else if (!lastOperator.equals(operator)) {
            fieldTextString.insert(0, '(').append(')');
        }

        lastOperator = operator;

        fieldTextString.append('+').append(operator).append('+');
    }

    /**
     * Applies a NOT operator to the {@code FieldTextBuilder}. Note that two calls to {@code NOT()} will cancel out.
     *
     * @return {@code this}
     */
    @Override
    public FieldTextBuilder NOT() {
        // NOT * => 0
        // NOT 0 => *

        if (isEmpty()) {
            return setFieldText(MATCHNOTHING);
        }

        if (MATCHNOTHING.equals(this)) {
            return clear();
        }

        not ^= true;
        return this;
    }

    /**
     * The total number of fieldtext specifiers in the current expression.
     *
     * @return The size of the expression.
     */
    @Override
    public int size() {
        return componentCount;
    }

    /**
     * Whether or not the builder is empty.
     *
     * @return {@code true} if and only if size is 0.
     */
    @Override
    public boolean isEmpty() {
        return componentCount == 0;
    }

    /**
     * The {@code String} representation of the fieldtext expression, exactly as it should be sent to IDOL.
     *
     * @return The {@code String} representation.
     */
    @Override
    public String toString() {
        if (!not) {
            return fieldTextString.toString();
        }

        if (componentCount == 1) {
            return new StringBuilder("NOT+").append(fieldTextString).toString();
        }

        return new StringBuilder("NOT(").append(fieldTextString).append(')').toString();
    }

    /**
     * Clears all fields back to an empty builder.
     *
     * @return {@code this}
     */
    public FieldTextBuilder clear() {
        fieldTextString.setLength(0);
        componentCount = 0;
        not = false;
        lastOperator = null;

        return this;
    }

    /**
     * Whether or not the builder is equal to {@link FieldTexts#MATCHNOTHING}.
     *
     * @return {@code true} if and only if {@code this} is equal to {@link FieldTexts#MATCHNOTHING MATCHNOTHING}
     */
    // TODO: Promote to the FieldText interface?
    public boolean isMatchNothing() {
        return equals(MATCHNOTHING);
    }

    /**
     * Converts a {@link FieldText} object into a {@code FieldTextBuilder}. This method can be more efficient than using
     * the equivalent constructor but the returned object might be backed by the {@link FieldText} object provided.
     *
     * @param fieldText The fieldtext expression to convert
     * @return An equivalent instance of {@code FieldTextBuilder}
     */
    public static FieldTextBuilder from(final FieldText fieldText) {
        return (fieldText instanceof FieldTextBuilder) ? (FieldTextBuilder)fieldText : new FieldTextBuilder(fieldText);
    }

}
