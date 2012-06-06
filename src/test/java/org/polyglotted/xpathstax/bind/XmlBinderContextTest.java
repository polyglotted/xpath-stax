package org.polyglotted.xpathstax.bind;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;


public class XmlBinderContextTest {

    @Test
    public void testBinder() {
        XmlBinderContext<Book> ctx = new XmlBinderContext<Book>(Book.class);
        assertNotNull(ctx);
        System.out.println(ctx);
    }
}
