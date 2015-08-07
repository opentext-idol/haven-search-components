/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */
package com.hp.autonomy.frontend.view.hod;

import java.util.Arrays;

/**
 * <p>Utility methods for working with fieldtext.
 *
 * <p>The factory methods for creating {@link FieldTextBuilder}s are included in this class as they share names with
 * instance methods of that class, which could lead to subtle problems accidentally invoking the static methods from
 * an instance. 
 */
public final class FieldTexts {
    /**
     * A fieldtext specifier that won't match any documents. This is useful when implementing boolean logic and an
     * empty initial state is required. It usually isn't necessary to access this field directly.
     *
     * <p>See also {@link FieldTextBuilder#isMatchNothing}.
     */
    public static final FieldText MATCHNOTHING = new MATCH("autn_date", "MATCHNOTHING");

    private FieldTexts() {
    }

    /**
     * Creates a new {@link FieldTextBuilder} and appends the specified fieldtext expressions, separated using AND
     * operators. If no arguments are specified then the return value will be an empty {@link FieldTextBuilder},
     * suitable for subsequent AND invocations.
     *
     * @param fieldTexts Fieldtext expressions
     * @return A new, empty {@link FieldTextBuilder}
     */
    public static FieldTextBuilder AND(final FieldText... fieldTexts) {
        return AND(Arrays.asList(fieldTexts));
    }

    /**
     * Creates a new {@link FieldTextBuilder} and appends the specified fieldtext expressions, separated using AND
     * operators. If no arguments are specified then the return value will be an empty {@link FieldTextBuilder},
     * suitable for subsequent AND invocations.
     *
     * @param fieldTexts Fieldtext expressions
     * @return A new, empty {@link FieldTextBuilder}
     */
    public static FieldTextBuilder AND(final Iterable<? extends FieldText> fieldTexts) {
        final FieldTextBuilder builder = new FieldTextBuilder();

        for (final FieldText fieldText : fieldTexts) {
            builder.AND(fieldText);
        }

        return builder;
    }

    /**
     * Creates a new {@link FieldTextBuilder} and appends the specified fieldtext expressions, separated using OR
     * operators. If no arguments are specified then the return value will be equal to
     * {@link FieldTexts#MATCHNOTHING}, suitable for subsequent OR invocations.
     *
     * @param fieldTexts Fieldtext expressions
     * @return A new {@link FieldTextBuilder}, equal to {@link FieldTexts#MATCHNOTHING MATCHNOTHING}
     */
    public static FieldTextBuilder OR(final FieldText... fieldTexts) {
        return OR(Arrays.asList(fieldTexts));
    }

    /**
     * Creates a new {@link FieldTextBuilder} and appends the specified fieldtext expressions, separated using OR
     * operators. If no arguments are specified then the return value will be equal to {@link FieldTexts#MATCHNOTHING},
     * suitable for subsequent OR invocations.
     *
     * @param fieldTexts Fieldtext expressions
     * @return A new {@link FieldTextBuilder}, equal to {@link FieldTexts#MATCHNOTHING MATCHNOTHING}
     */
    public static FieldTextBuilder OR(final Iterable<? extends FieldText> fieldTexts) {
        final FieldTextBuilder builder = new FieldTextBuilder(MATCHNOTHING);

        for (final FieldText fieldText : fieldTexts) {
            builder.OR(fieldText);
        }

        return builder;
    }

    /**
     * Creates a new {@link FieldTextBuilder} and appends the specified fieldtext expressions, separated using XOR
     * operators. If no arguments are specified then the return value will be equal to {@link FieldTexts#MATCHNOTHING},
     * suitable for subsequent XOR invocations.
     *
     * @param fieldTexts Fieldtext expressions
     * @return A new {@link FieldTextBuilder}, equal to {@link FieldTexts#MATCHNOTHING MATCHNOTHING}
     */
    public static FieldTextBuilder XOR(final FieldText... fieldTexts) {
        return XOR(Arrays.asList(fieldTexts));
    }

    /**
     * Creates a new {@link FieldTextBuilder} and appends the specified fieldtext expressions, separated using XOR
     * operators. If no arguments are specified then the return value will be equal to {@link FieldTexts#MATCHNOTHING},
     * suitable for subsequent XOR invocations.
     *
     * @param fieldTexts Fieldtext expressions
     * @return A new {@link FieldTextBuilder}, equal to {@link FieldTexts#MATCHNOTHING MATCHNOTHING}
     */
    public static FieldTextBuilder XOR(final Iterable<? extends FieldText> fieldTexts) {
        final FieldTextBuilder builder = new FieldTextBuilder(MATCHNOTHING);

        for (final FieldText fieldText : fieldTexts) {
            builder.XOR(fieldText);
        }

        return builder;
    }

    /**
     * Wraps the specified fieldtext in a {@link FieldTextBuilder} and invokes the NOT operator upon it.
     *
     * @param fieldText A fieldtext expression
     * @return A new {@code FieldTextBuilder}
     */
    public static FieldTextBuilder NOT(final FieldText fieldText) {
        return new FieldTextBuilder(fieldText).NOT();
    }
}
