package org.polyglotted.xpathstax.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.polyglotted.xpathstax.data.Value;

public class XmlAttributeTest {

    private static final String attributeStr = "realm=\"test\" id='admin' name='Administrator'";

    @Test
    public void testSingle() {
        String key = "numberFound";
        XmlAttribute attribute = XmlAttribute.from(key);
        assertEquals(1, attribute.count());
        assertTrue(attribute.contains(key));
        assertNotNull(attribute.get(key).asString());
    }

    @Test
    public void testMulti() {
        XmlAttribute attribute = XmlAttribute.from(attributeStr);
        assertEquals(3, attribute.count());
        assertTrue(attribute.contains("realm"));
        assertTrue(attribute.contains("id"));
        assertTrue(attribute.contains("name", "Administrator"));
        assertEquals("test", attribute.get("realm").asString());
        assertEquals("Administrator", attribute.get("name").asString());
    }

    @Test
    public void testContains() {
        XmlAttribute outer = XmlAttribute.from(attributeStr);
        XmlAttribute outer2 = XmlAttribute.from(attributeStr);
        XmlAttribute inner1 = XmlAttribute.from("id=\"admin\"");
        XmlAttribute inner2 = XmlAttribute.from("realm=\"test\" name='Administrator'");
        assertTrue(outer.contains(outer2));
        assertTrue(outer.contains(inner1));
        assertTrue(outer.contains(inner1));
        assertTrue(outer.contains(inner2));
        assertFalse(inner2.contains(outer));
    }

    @Test
    public void testIterate() {
        XmlAttribute attribute = XmlAttribute.from(attributeStr);
        Iterator<Map.Entry<String, Value>> iter = attribute.iterate().iterator();

        verify(iter.next(), "realm", "test");
        verify(iter.next(), "id", "admin");
        verify(iter.next(), "name", "Administrator");
    }

    private void verify(Entry<String, Value> entry, String key, String value) {
        assertEquals(key, entry.getKey());
        assertEquals(value, entry.getValue().asString());
    }
}