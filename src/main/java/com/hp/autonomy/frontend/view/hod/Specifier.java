/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */
package com.hp.autonomy.frontend.view.hod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.autonomy.aci.client.util.AciURLCodec;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

/**
 * A class to represent a general fieldtext specifier of the form:
 *
 * <pre>OPERATOR{values}:fields</pre>
 *
 * In the most general case, the <tt>values</tt> consist of comma-separated, URL-encoded <tt>Strings</tt> and the
 * <tt>fields</tt> are colon-separated <tt>Strings</tt>. Subclasses may wish to impose further restrictions on
 * the forms of these aspects of the specifier, for example allowing only numeric values. As there is no reliable way to
 * distinguish between colons in XML namespaces and colons in metafields (e.g. autn:date), only namespaces will be
 * escaped automatically. To use a metafield the colon must be escaped to an underscore manually.
 * <p/>
 * It should not usually be necessary to instantiate this class directly as subclasses exist for all common fieldtext
 * operators. This class and its known subclasses are all immutable but each class provides a number of constructors to
 * make instantiation as convenient as possible.
 *
 * @author darrelln
 * @author boba
 * @author williamo
 * @version $Revision: #5 $ $Date: 2014/12/10 $
 */
class Specifier extends AbstractFieldText {

    private final List<String> fields;

    private final List<String> values;

    /**
     * The fieldtext operator for the specifier. It will be in uppercase and interned, allowing == comparison to String
     * literals.
     */
    public final String OPERATOR;

    /**
     * Creates a new specifier. e.g.
     *
     * <pre>    new Specifier("OPERATOR", "TITLE", "1 2", "3 4").toString();</pre>
     *
     * evaluates to:
     *
     * <pre>    "OPERATOR{1%202,3%204}:TITLE"</pre>
     *
     * @param operator The fieldtext operator of the specifier.
     * @param field The name of the IDOL field.
     * @param values A <tt>String...</tt> of field values.
     */
    public Specifier(final String operator, final String field, final String... values) {
        this(operator, Arrays.asList(field), toIterable(values));
    }

    /**
     * Creates a new specifier.
     *
     * @param operator The fieldtext operator of the specifier.
     * @param field The name of the IDOL field.
     * @param values An <tt>Iterable</tt> of field values.
     */
    public Specifier(final String operator, final String field, final Iterable<? extends String> values) {
        this(operator, Arrays.asList(field), values);
    }

    /**
     * Creates a new specifier. Colons in field names will be converted to underscores. e.g.
     *
     * <pre>    new Specifier("OPERATOR", new String[]{"A", "B"}, new String[]{"1 2", "3 4"}).toString();</pre>
     *
     * evaluates to:
     *
     * <pre>    "OPERATOR{1%202,3%204}:A:B"</pre>
     *
     * @param operator The fieldtext operator of the specifier.
     * @param fields A <tt>String[]</tt> of field names.
     * @param values A <tt>String...</tt> of field values.
     */
    public Specifier(final String operator, final String[] fields, final String... values) {
        this(operator, toIterable(fields), toIterable(values));
    }

    /**
     * Creates a new specifier. Colons in field names will be converted to underscores.
     *
     * @param operator The fieldtext operator of the specifier.
     * @param fields A <tt>String[]</tt> of field names.
     * @param values An <tt>Iterable</tt> of field values.
     */
    public Specifier(final String operator, final String[] fields, final Iterable<? extends String> values) {
        this(operator, toIterable(fields), values);
    }

    /**
     * Creates a new specifier. Colons in field names will be converted to underscores. e.g.
     *
     * <pre>
     *     List&lt;String&gt; fields = new ArrayList&lt;String&gt;();
     *     fields.add("A");
     *     fields.add("B:C");
     *
     *     new Specifier("OPERATOR", fields, "yes", "no").toString();
     * </pre>
     *
     * evaluates to:
     *
     * <pre>    "OPERATOR{yes,no}:A:B_C"</pre>
     *
     * @param operator The fieldtext operator of the specifier.
     * @param fields An <tt>Iterable</tt> of field names.
     * @param values A <tt>String...</tt> of field values.
     */
    public Specifier(final String operator, final Iterable<? extends String> fields, final String... values) {
        this(operator, fields, toIterable(values));
    }

    /**
     * Creates a new specifier. Colons in field names will be converted to underscores.
     *
     * @param operator The fieldtext operator for the specifier.
     * @param fields An <tt>Iterable</tt> of field names.
     * @param values An <tt>Iterable</tt> of field values.
     */
    public Specifier(final String operator, final Iterable<? extends String> fields, final Iterable<? extends String> values) {
        Validate.isTrue(StringUtils.isNotBlank(operator), "Operator must not be blank");
        Validate.notNull(fields, "Fields must not be null");
        Validate.notNull(values, "Values must not be null");

        this.fields = Collections.unmodifiableList(resolveFields(fields));

        Validate.notEmpty(this.fields, "No valid fields were specified");

        OPERATOR = operator.trim().toUpperCase(Locale.ENGLISH).intern();

        this.values = Collections.unmodifiableList(resolveValues(values));
    }

    /**
     * Private helper function for converting a String[] to an Iterable.
     *
     * @param strings A String[] or null.
     * @return An Iterable or null.
     */
    private static Iterable<String> toIterable(final String[] strings) {
        if (strings == null) {
            return null;
        }

        return Arrays.asList(strings);
    }

    /**
     * Private helper method for setting the field names. It converts colons to underscores and removes any blanks or
     * excess whitespace. Instances are immutable so this method cannot be made public.
     *
     * @param fields A non-null Iterable of field names.
     */
    private static List<String> resolveFields(final Iterable<? extends String> fields) {
        final List<String> fieldList = new ArrayList<String>();

        for (final String field : fields) {
            Validate.isTrue(StringUtils.isNotBlank(field), "One of the specified fields was blank");
            fieldList.add(field.trim());
        }

        return fieldList;
    }

    /**
     * Private helper method for setting the field values. nulls are not permitted. Instances are immutable so this
     * method cannot be made public.
     *
     * @param values A non-null Iterable of field values.
     */
    private static List<String> resolveValues(final Iterable<? extends String> values) {
        final List<String> valuesList = new ArrayList<String>();

        for(final String value : values) {
            Validate.notNull(value, "One of the specified values was null");
            valuesList.add(value);
        }

        return valuesList;
    }

    /**
     * All specifiers have a size of <tt>1</tt>.
     *
     * @return <tt>1</tt>.
     */
    @Override
    public final int size() {
        return 1;
    }

    /**
     * Specifiers cannot be empty.
     *
     * @return <tt>false</tt>.
     */
    @Override
    public final boolean isEmpty() {
        return false;
    }

    /**
     * Accessor for the <tt>OPERATOR</tt> field.
     *
     * @return The <tt>OPERATOR</tt> field.
     */
    public final String getOperator() {
        return OPERATOR;
    }

    /**
     * Accessor for the colon-separated, <tt>String</tt> representation of the field names for this specifier.
     *
     * @return The combined field names.
     */
    private String getFieldsString() {
        final StringBuilder fieldsString = new StringBuilder();

        for (final String field : fields) {
            // XML namespaces require that the colon be percent-escaped but nothing else
            fieldsString.append(':').append(field.replace(":", "%3A"));
        }

        return fieldsString.toString();
    }

    /**
     * Accessor for the list of field names for this specifier. Some specifiers
     * place restrictions on the number of fields they require and may provide
     * more suitable accessors for accessing the field names.
     *
     * As instances are immutable, the returned list does not support modifications.
     *
     * @return The list of field names.
     */
    public final List<String> getFields() {
        return fields;
    }

    /**
     * Accessor for the specifier's values. The values are URL encoded and
     * separated by commas.
     *
     * @return The specifier's value.
     */
    private String getValuesString() {
        final StringBuilder builder = new StringBuilder();

        for(final String value : values) {
            builder.append(AciURLCodec.getInstance().encode(value)).append(',');
        }

        if(builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    /**
     * Accessor for the list of field values for this specifier. An empty value
     * could be signified by either an empty list or a list containing just the
     * empty <tt>String</tt> - neither the list nor its values should ever be
     * <tt>null</tt>.
     *
     * As instances are immutable, the returned list does not support modifications.
     *
     * @return A clone of the list of field values.
     */
    public final List<String> getValues() {
        return values;
    }

    /**
     * The <tt>String</tt> representation of the specifier, as it should be sent
     * to IDOL.
     *
     * @return An IDOL fieldtext <tt>String</tt>.
     */
    @Override
    public final String toString() {
        return new StringBuilder(OPERATOR).append('{').append(getValuesString()).append('}').append(getFieldsString()).toString();
    }

    @Override
    public final FieldText AND(final FieldText fieldText) {
        return super.AND(fieldText);
    }

    @Override
    public final FieldText OR(final FieldText fieldText) {
        return super.OR(fieldText);
    }

    @Override
    public final FieldText NOT() {
        return super.NOT();
    }

    @Override
    public final FieldText XOR(final FieldText fieldText) {
        return super.XOR(fieldText);
    }

    // For now we only allow Specifier WHEN Specifier
    @Override
    public final FieldText WHEN(final FieldText fieldText) {
        return super.WHEN(fieldText);
    }

    // For now we only allow Specifier WHENn Specifier
    @Override
    public final FieldText WHEN(final int depth, final FieldText fieldText) {
        return super.WHEN(depth, fieldText);
    }

    @Override
    public final boolean equals(final Object obj) {
        return super.equals(obj);
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

}
