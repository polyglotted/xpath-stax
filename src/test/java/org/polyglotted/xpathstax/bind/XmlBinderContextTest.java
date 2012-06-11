package org.polyglotted.xpathstax.bind;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.polyglotted.xpathstax.bind.XmlBinderContext.ClassContext;

public class XmlBinderContextTest {

    @Test
    public void testBinder() {
        XmlBinderContext<Book> ctx = new XmlBinderContext<Book>(Book.class);
        assertNotNull(ctx);

        Map<String, Class<?>> types = ctx.getTypes();
        checkNames(types, "book", "revision", "description");
        
        Map<Class<?>, ClassContext> contexts = ctx.getContexts();
        checkClasses(contexts, Book.class, Revision.class, Desc.class);
        
        ClassContext bookCtx = contexts.get(Book.class);
        checkAttributes(bookCtx, "id");
        checkElements(bookCtx, "title", "author", "price", "description", "genre");
        checkCollections(bookCtx, "type", "revision");

        ClassContext revCtx = contexts.get(Revision.class);
        checkAttributes(revCtx, "year", "definition");
        checkXmlValue(revCtx);

        ClassContext descCtx = contexts.get(Desc.class);
        checkXmlValue(descCtx);
    }

    private void checkNames(Map<String, Class<?>> types, String... names) {
        for(String name : names)
            assertTrue(types.containsKey(name));
    }

    private void checkClasses(Map<Class<?>, ClassContext> contexts, Class<?>... classes) {
        for (Class<?> clazz : classes)
            assertTrue(contexts.containsKey(clazz));
    }

    private void checkAttributes(ClassContext clsCtx, String... atts) {
        for (String att : atts)
            assertTrue(clsCtx.attributes.containsKey(att));
    }

    private void checkElements(ClassContext clsCtx, String... elems) {
        for (String elem : elems)
            assertTrue(clsCtx.elements.containsKey(elem));
    }

    private void checkCollections(ClassContext clsCtx, String... colls) {
        for (String col : colls)
            assertTrue(clsCtx.collections.containsKey(col));
    }

    private void checkXmlValue(ClassContext clsCtx) {
        assertNotNull(clsCtx.getXmlValueField());
    }
}
