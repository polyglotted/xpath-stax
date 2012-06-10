package org.polyglotted.xpathstax;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Ignore;
import org.junit.Test;
import org.polyglotted.xpathstax.api.NodeHandler;
import org.polyglotted.xpathstax.bind.Book;
import org.polyglotted.xpathstax.bind.Desc;
import org.polyglotted.xpathstax.bind.NodeConverter;
import org.polyglotted.xpathstax.model.Value;
import org.polyglotted.xpathstax.model.XPathRequest;
import org.polyglotted.xpathstax.model.XmlNode;

import com.google.common.util.concurrent.AtomicDouble;

public class XPathStaxParserTest {

    @Test
    @Ignore
    public void testParse() {

        XPathStaxParser parser = new XPathStaxParser();

        final AtomicDouble resultCount = new AtomicDouble();
        parser.addHandler(new XPathRequest("/catalog/book[@id='bk101']/price"), new NodeHandler() {
            @Override
            public void processNode(XmlNode xmlNode) {
                Value count = xmlNode.getText();
                resultCount.set(count.asDouble(0));
            }

            @Override
            public void elementStart(String elementName) {
            }
        });
        parser.parse(asStream("testxmls/books.xml"));

        assertEquals(44.95, resultCount.get(), 0.001);
    }

    @Test
    public void testParseBook() {

        XPathStaxParser parser = new XPathStaxParser();

        final AtomicReference<Book> ref = new AtomicReference<Book>();
        parser.addHandler(new NodeConverter<Book>("/catalog/book[@id='bk101']/*") {
            @Override
            public void process(Book object) {
                ref.set(object);
            }
        });
        parser.parse(asStream("testxmls/books.xml"));

        Book book = ref.get();
        assertBookDetails(book, "bk101", "Gambardella, Matthew", "XML Developer's Guide", 44.95);
        assertEquals(newHashSet("Softback", "Bounded", "Hardcover"), book.getTypes());
        assertDescDetails(book.getDescription(), "");
    }

    private void assertBookDetails(Book book, String id, String author, String title, double price) {
        assertNotNull(book);
        assertEquals(id, book.getId());
        assertEquals(author, book.getAuthor());
        assertEquals(title, book.getTitle());
        assertEquals(price, book.getPrice(), 0.001);
    }

    private void assertDescDetails(Desc desc, String description) {
        assertNotNull(desc);
    }

    // TODO multi-thread test

    protected InputStream asStream(String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
    }
}