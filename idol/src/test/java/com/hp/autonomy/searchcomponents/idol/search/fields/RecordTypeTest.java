package com.hp.autonomy.searchcomponents.idol.search.fields;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.w3c.dom.Element;

import jakarta.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RecordTypeTest {
    /**
     * Parses XML into {@link FieldContainer}.
     */
    private Jaxb2Marshaller xmlMarshaller;


    /**
     * Target for XML parser - input is '<container><field>...</field></container>'.
     */
    @XmlRootElement(name = "container")
    private static final class FieldContainer {
        /**
         * Inner parsed XML, as an {@link Element}.  This node is passed into the record-type field
         * parser.
         */
        public Object field;
    }


    /**
     * Parse XML into a {@link org.w3c.dom.Node}, then use the method under test to parse it as a
     * record-type field.
     *
     * @param innerXml
     * @return
     */
    private Serializable parseXml(final String innerXml) {
        final String xml = "<container>" + innerXml + "</container>";
        final InputStream fieldStream =
            new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        final FieldContainer containerNode =
            (FieldContainer) xmlMarshaller.unmarshal(new StreamSource(fieldStream));
        return RecordType.parseValue((Element) containerNode.field);
    }

    /**
     * Used with {@link #buildChildren} to construct values for making assertions against parsed
     * record-type fields.
     *
     * @param name Child node or attribute name
     * @param values Child node or attribute values
     * @return To be passed into {@link #buildChildren}
     */
    private Map.Entry<String, Serializable> buildChild(
        final String name, final Serializable... values
    ) {
        return new AbstractMap.SimpleEntry<>(name, new ArrayList<>(Arrays.asList(values)));
    }

    /**
     * Construct a value for making an assertion against parsed record-type fields.
     *
     * @param childNodes Results from {@link #buildChild}
     * @return Node as expected to be returned from the record-type field parser
     */
    private HashMap<String, Serializable> buildChildren(
        final Map.Entry<String, Serializable>... childNodes
    ) {
        final HashMap<String, Serializable> children = new HashMap<>();
        for (final Map.Entry<String, Serializable> childNode : childNodes) {
            children.put(childNode.getKey(), childNode.getValue());
        }
        return children;
    }

    @Before
    public void setUp() {
        xmlMarshaller = new Jaxb2Marshaller();
        xmlMarshaller.setClassesToBeBound(FieldContainer.class);
    }

    @Test
    public void testText() {
        final Serializable result = parseXml("<field>text</field>");
        Assert.assertEquals("should return string containing node text", "text", result);
    }

    @Test
    public void testEmpty() {
        final Serializable result = parseXml("<field></field>");
        Assert.assertEquals("should return empty string", "", result);
    }

    @Test
    public void testChildren() {
        final Serializable result = parseXml("" +
            "<field>" +
            "    <first>child 1</first>" +
            "    <second>child 2</second>" +
            "</field>");

        Assert.assertEquals("should return map of children", buildChildren(
            buildChild("first", "child 1"),
            buildChild("second", "child 2")
        ), result);
    }

    @Test
    public void testAttributes() {
        final Serializable result = parseXml("<field first=\"attr 1\" second=\"attr 2\"></field>");
        Assert.assertEquals("should return map of attributes with prefixed names", buildChildren(
            buildChild("@first", "attr 1"),
            buildChild("@second", "attr 2")
        ), result);
    }

    @Test
    public void testUppercaseNames() {
        final Serializable result = parseXml("" +
            "<field ATTR=\"attr val\">" +
            "    <CHILD>child val</CHILD>" +
            "</field>");

        Assert.assertEquals("should lowercase child and attribute names", buildChildren(
            buildChild("@attr", "attr val"),
            buildChild("child", "child val")
        ), result);
    }

    @Test
    public void testChildrenAndText() {
        final Serializable result = parseXml("" +
            "<field> before" +
            "    <first>child 1</first> between" +
            "    <second>child 2</second> after" +
            "</field>");

        Assert.assertEquals("should ignore node text", buildChildren(
            buildChild("first", "child 1"),
            buildChild("second", "child 2")
        ), result);
    }

    @Test
    public void testAttributesAndText() {
        final Serializable result = parseXml(
            "<field first=\"attr 1\" second=\"attr 2\">some text</field>");
        Assert.assertEquals("should ignore node text", buildChildren(
            buildChild("@first", "attr 1"),
            buildChild("@second", "attr 2")
        ), result);
    }

    @Test
    public void testChildWithMultipleValues() {
        final Serializable result = parseXml("" +
            "<field>" +
            "    <first>child 1a</first>" +
            "    <first>child 1b</first>" +
            "    <second>child 2</second>" +
            "    <first>child 1c</first>" +
            "</field>");

        Assert.assertEquals("should return all values in child array", buildChildren(
            buildChild("first", "child 1a", "child 1b", "child 1c"),
            buildChild("second", "child 2")
        ), result);
    }

    @Test
    public void testChildWithMultipleValuesWithDifferentCases() {
        final Serializable result = parseXml("" +
            "<field>" +
            "    <first>child 1a</first>" +
            "    <FIRST>child 1b</FIRST>" +
            "    <second>child 2</second>" +
            "    <First>child 1c</First>" +
            "</field>");

        Assert.assertEquals("should return all values in child array", buildChildren(
            buildChild("first", "child 1a", "child 1b", "child 1c"),
            buildChild("second", "child 2")
        ), result);
    }

    @Test
    public void testAttributeWithMultipleValuesWithDifferentCases() {
        final Serializable result = parseXml("" +
            "<field " +
            "    first=\"attr 1a\" " +
            "    FIRST=\"attr 1b\" " +
            "    First=\"attr 1c\">" +
            "</field>");

        // attributes aren't in source order
        Assert.assertTrue(result instanceof Map);
        final Map<?, ?> resultMap = (Map<?, ?>) result;
        Assert.assertEquals(1, resultMap.size());
        Assert.assertTrue(resultMap.containsKey("@first"));
        Assert.assertTrue(resultMap.get("@first") instanceof List);
        final List<?> attrList = (List<?>) resultMap.get("@first");
        Assert.assertEquals(3, attrList.size());
        Assert.assertTrue(attrList.contains("attr 1a"));
        Assert.assertTrue(attrList.contains("attr 1b"));
        Assert.assertTrue(attrList.contains("attr 1c"));
    }

    @Test
    public void testChildWithChildrenAndAttributes() {
        final Serializable result = parseXml("" +
            "<field>" +
            "    <outer attr=\"attr val\">before</outer>" +
            "    <outer>" +
            "        <inner thing=\"stuff1\"><innerinner /></inner>" +
            "        <other>the text</other>" +
            "        <inner thing=\"stuff2\"></inner>" +
            "    </outer>" +
            "    <outer>after</outer>" +
            "</field>");

        Assert.assertEquals("should return all values in child array", buildChildren(
            buildChild("outer",
                buildChildren(buildChild("@attr", "attr val")),
                buildChildren(
                    buildChild("inner",
                        buildChildren(
                            buildChild("@thing", "stuff1"),
                            buildChild("innerinner", "")
                        ),
                        buildChildren(buildChild("@thing", "stuff2"))
                    ),
                    buildChild("other", "the text")
                ),
                "after"
            )
        ), result);
    }

}
