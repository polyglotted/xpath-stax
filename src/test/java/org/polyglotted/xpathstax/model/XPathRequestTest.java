package org.polyglotted.xpathstax.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.polyglotted.xpathstax.model.XmlAttribute.EMPTY;

import org.junit.Test;
import org.polyglotted.xpathstax.api.AttributeProvider;

public class XPathRequestTest {

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidChildRequest() {
        new XPathRequest("/response/*/documents");
    }

    @Test
    public void testCanProcessSimple() {
        String expected = "/response/documents";
        XPathRequest req = new XPathRequest(expected);
        assertTrue(req.canProcess(expected, EMPTY, null));
    }

    @Test
    public void testCanProcessChildren() {
        String expected = "/response/documents/*";
        XPathRequest req = new XPathRequest(expected);
        assertFalse(req.canProcess("/response", EMPTY, null));
        assertTrue(req.canProcess("/response/documents", EMPTY, null));
        assertTrue(req.canProcess("/response/documents/document", EMPTY, null));
        assertTrue(req.canProcess("/response/documents/document/fields", EMPTY, null));
        assertTrue(req.canProcess("/response/documents/document/fields/field", EMPTY, null));
    }

    @Test
    public void testCanProcessWithAttribute() {
        String expected = "/response/documents";
        XmlAttribute attribute = XmlAttribute.from("numberFound=12345");
        AttributeProvider provider = testProvider(expected, attribute);
        String childElem = "/response/documents/document";
        AttributeProvider failProvider = testProvider(expected, EMPTY);

        XPathRequest req = new XPathRequest("/response/documents[@numberFound]/*");

        assertTrue(req.canProcess(expected, attribute, failProvider));
        assertTrue(req.canProcess(expected, EMPTY, provider));
        assertTrue(req.canProcess(childElem, EMPTY, provider));
        assertFalse(req.canProcess(childElem, EMPTY, failProvider));
    }

    @Test
    public void testCanProcessWithElementAttribute() {
        String expected = "/response/documents/document/fields";

        XPathRequest req = new XPathRequest("/response/documents/document/fields[@count]");

        assertTrue(req.canProcess(expected, XmlAttribute.from("count=12345"), testProvider("", EMPTY)));
    }

    @Test
    public void testCanProcessWithAttributeValue() {
        String expected = "/response/documents/document/fields/field/value";
        String childElem = "/response/documents/document/fields/field/value/string";
        AttributeProvider provider = testProvider("/response/documents/document/fields/field",
                        XmlAttribute.from("name='.id'"));
        AttributeProvider failProvider = testProvider("/response/documents/document/fields",
                        XmlAttribute.from("name='.id'"));

        XPathRequest req = new XPathRequest("/response/documents/document/fields/field[@name='.id']/value");

        assertTrue(req.canProcess(expected, EMPTY, provider));
        assertFalse(req.canProcess(expected, EMPTY, failProvider));
        assertFalse(req.canProcess(childElem, EMPTY, provider));
    }

    private AttributeProvider testProvider(final String elem, final XmlAttribute attribute) {
        return new AttributeProvider() {
            @Override
            public XmlAttribute getAttribute(String attribElem) {
                if (elem.equals(attribElem)) {
                    return attribute;
                }
                return EMPTY;
            }
        };
    }
}