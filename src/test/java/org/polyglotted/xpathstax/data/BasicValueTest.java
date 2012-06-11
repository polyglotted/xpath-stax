package org.polyglotted.xpathstax.data;

import static java.text.DateFormat.getDateTimeInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.Thread.State;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.polyglotted.xpathstax.bind.Book.Genre;

public class BasicValueTest {

    @Test
    public void testAsBigDecimal() {
        assertNull(new BasicValue(null).asBigDecimal(null));
        assertEquals(BigDecimal.valueOf(2.0), new BasicValue(null).asBigDecimal(BigDecimal.valueOf(2.0)));
        assertEquals(BigDecimal.valueOf(2.0), new BasicValue(BigDecimal.valueOf(2.0)).asBigDecimal(null));
    }

    @Test
    public void testAsBigInteger() {
        assertNull(new BasicValue(null).asBigInteger(null));
        assertEquals(BigInteger.valueOf(2l), new BasicValue(null).asBigInteger(BigInteger.valueOf(2l)));
        assertEquals(BigInteger.valueOf(2l), new BasicValue(BigInteger.valueOf(2l)).asBigInteger(null));
    }

    @Test
    public void testAsDate() throws Exception {
        final String datePattern = "dd-MM-yyyy";
        final Date date = new Date();

        assertNull(new BasicValue(null).asDate(null));
        assertNull(new BasicValue(null).asDate(null, datePattern));
        assertEquals(date, new BasicValue(null).asDate(date));
        assertEquals(date, new BasicValue(date).asDate(null));
        assertNotNull(new BasicValue(getDateTimeInstance().format(date)).asDate(null));
        
        Date date2 = new SimpleDateFormat(datePattern).parse("20-10-2010");
        assertEquals(date2, new BasicValue("20-10-2010").asDate(null, datePattern));
    }

    @Test
    public void testAsEnum() {
        assertNull(new BasicValue(null).asEnum(State.class, null));
        assertEquals(State.RUNNABLE, new BasicValue(null).asEnum(State.class, State.RUNNABLE));
        assertEquals(State.RUNNABLE, new BasicValue(State.RUNNABLE).asEnum(State.class, null));
        assertEquals(State.RUNNABLE, new BasicValue("RUNNABLE").asEnum(State.class, null));
        assertNull(new BasicValue("RUNNING").asEnum(State.class, null));
        
        assertEquals(Genre.Computer, new BasicValue("Computer Science").asEnum(Genre.class, null));
    }

}
