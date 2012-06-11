package org.polyglotted.xpathstax.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PrimitiveValueTest {

    @Test
    public void testAsBoolean() {
        assertNull(new PrimitiveValue(null).asBoolean(null));
        assertFalse(new PrimitiveValue(null).asBoolean());
        assertTrue(new PrimitiveValue(null).asBoolean(true));

        assertTrue(new PrimitiveValue("true").asBoolean(null));
        assertFalse(new PrimitiveValue("false").asBoolean(null));
        assertFalse(new PrimitiveValue("hello").asBoolean(null));
    }

    @Test
    public void testAsCharacter() {
        assertNull(new PrimitiveValue(null).asCharacter(null));
        assertEquals((char) 0, new PrimitiveValue(null).asChar());
        assertEquals(Character.valueOf('2'), new PrimitiveValue(null).asCharacter('2'));

        assertEquals(Character.valueOf('2'), new PrimitiveValue('2').asCharacter(null));
    }

    @Test
    public void testAsByte() {
        assertNull(new PrimitiveValue(null).asByte(null));
        assertEquals((byte) 0, new PrimitiveValue(null).asByte());
        assertEquals(Byte.valueOf("2"), new PrimitiveValue(null).asByte((byte) 2));

        assertEquals(Byte.valueOf("2"), new PrimitiveValue((byte) 2).asByte(null));
        assertEquals(Byte.valueOf("2"), new PrimitiveValue("2").asByte(null));
        assertEquals(Byte.valueOf("2"), new PrimitiveValue(new Byte("2")).asByte(null));
    }

    @Test
    public void testAsShort() {
        assertNull(new PrimitiveValue(null).asShort(null));
        assertEquals((short) 0, new PrimitiveValue(null).asShort());
        assertEquals(Short.valueOf("2"), new PrimitiveValue(null).asShort((short) 2));

        assertEquals(Short.valueOf("2"), new PrimitiveValue((short) 2).asShort(null));
        assertEquals(Short.valueOf("2"), new PrimitiveValue("2").asShort(null));
        assertEquals(Short.valueOf("2"), new PrimitiveValue(new Short("2")).asShort(null));
    }

    @Test
    public void testAsInteger() {
        assertNull(new PrimitiveValue(null).asInteger(null));
        assertEquals(0, new PrimitiveValue(null).asInt());
        assertEquals(Integer.valueOf("2"), new PrimitiveValue(null).asInteger(2));

        assertEquals(Integer.valueOf("2"), new PrimitiveValue((short) 2).asInteger(null));
        assertEquals(Integer.valueOf("2"), new PrimitiveValue("2").asInteger(null));
        assertEquals(Integer.valueOf("2"), new PrimitiveValue(new Integer("2")).asInteger(null));
    }

    @Test
    public void testAsLong() {
        assertNull(new PrimitiveValue(null).asLong(null));
        assertEquals((short) 0, new PrimitiveValue(null).asLong());
        assertEquals(Long.valueOf("2"), new PrimitiveValue(null).asLong(2l));

        assertEquals(Long.valueOf("2"), new PrimitiveValue(2l).asLong(null));
        assertEquals(Long.valueOf("2"), new PrimitiveValue("2").asLong(null));
        assertEquals(Long.valueOf("2"), new PrimitiveValue(new Long("2")).asLong(null));
    }

    @Test
    public void testAsFloat() {
        assertNull(new PrimitiveValue(null).asFloat(null));
        assertEquals(0f, new PrimitiveValue(null).asFloat(), 0.01);
        assertEquals(Float.valueOf("2"), new PrimitiveValue(null).asFloat(2f));

        assertEquals(Float.valueOf("2"), new PrimitiveValue(2f).asFloat(null));
        assertEquals(Float.valueOf("2"), new PrimitiveValue("2").asFloat(null));
        assertEquals(Float.valueOf("2"), new PrimitiveValue(new Float("2")).asFloat(null));
    }

    @Test
    public void testAsDouble() {
        assertNull(new PrimitiveValue(null).asDouble(null));
        assertEquals(0, new PrimitiveValue(null).asDouble(), 0.01);
        assertEquals(Double.valueOf("2"), new PrimitiveValue(null).asDouble(2d));

        assertEquals(Double.valueOf("2"), new PrimitiveValue(2d).asDouble(null));
        assertEquals(Double.valueOf("2"), new PrimitiveValue("2").asDouble(null));
        assertEquals(Double.valueOf("2"), new PrimitiveValue(new Double("2")).asDouble(null));
    }

}
