package org.polyglotted.xpathstax;

import org.polyglotted.xpathstax.api.NodeHandler;
import org.polyglotted.xpathstax.bind.Book;
import org.polyglotted.xpathstax.bind.Book.Genre;
import org.polyglotted.xpathstax.bind.Book.Type;
import org.polyglotted.xpathstax.bind.Desc;
import org.polyglotted.xpathstax.bind.NodeConverter;
import org.polyglotted.xpathstax.bind.Revision;
import org.polyglotted.xpathstax.bind.Revision.Definition;
import org.polyglotted.xpathstax.data.Value;
import org.polyglotted.xpathstax.model.XPathRequest;
import org.polyglotted.xpathstax.model.XmlNode;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.testng.Assert.*;

public class XPathStaxParserTest {

    @Test
    public void testParse() {
        XPathStaxParser parser = new XPathStaxParser();
        parser.addHandler(new XPathRequest("/catalog/book[@id='bk101']/price"), new NodeHandler() {
            @Override
            public void processNode(XmlNode xmlNode) {
                Value count = xmlNode.getText();
                assertEquals(44.95, count.asDouble(), 0.001);
            }

            @Override
            public void elementStart(String elementName) {
                assertEquals("price", elementName);
            }
        });
        parser.parse(asStream("testxmls/books.xml"));
    }

    @Test
    public void testParseAllBooks() {

        XPathStaxParser parser = new XPathStaxParser();

        final List<Book> books = newArrayList();
        parser.addHandler(new NodeConverter<Book>("/catalog/book/*") {
            @Override
            public void process(Book object) {
                books.add(object);
            }
        });
        parser.parse(asStream("testxmls/books.xml"));
        assertEquals(10, books.size());
    }

    @Test
    public void testParseBook() {

        XPathStaxParser parser = new XPathStaxParser();

        final AtomicReference<Book> ref = new AtomicReference<>();
        parser.addHandler(new NodeConverter<Book>("/catalog/book[@id='bk101']/*") {
            @Override
            public void process(Book object) {
                ref.set(object);
            }
        });
        parser.parse(asStream("testxmls/books.xml"));

        Book book = ref.get();
        assertBookDetails(book, "bk101", "Gambardella, Matthew", "XML Developer's Guide", 44.95, Genre.Computer);

        assertTrue(newHashSet(Type.Softback, Type.Hardcover, Type.Bounded).containsAll(book.getTypes()));
        assertTrue(newArrayList("this book is brilliant, the sunday times", "this book is a must read, the economist")
            .containsAll(book.getComments()));
        assertDescDetails(book.getDescription(), "An in-depth look at creating applications with XML.");
        assertRevisions(book.getRevisions());
    }

    private void assertBookDetails(Book book, String id, String author, String title, double price, Genre genre) {
        assertNotNull(book);
        assertEquals(id, book.getId());
        assertEquals(author, book.getAuthor());
        assertEquals(title, book.getTitle());
        assertEquals(price, book.getPrice(), 0.001);
        assertEquals(genre, book.getGenre());
    }

    private void assertDescDetails(Desc desc, String description) {
        assertNotNull(desc);
        assertEquals(description, clean(desc.getDescription()));
    }

    private void assertRevisions(List<Revision> revisions) {
        assertNotNull(revisions);
        assertEquals(2, revisions.size());
        checkRevision(revisions.get(0), 2000, "1st Ed", Definition.Biblio);
        checkRevision(revisions.get(1), 2002, "2nd Ed", Definition.Collector);
    }

    private void checkRevision(Revision rev, int year, String id, Definition definition) {
        assertEquals(year, rev.getYear());
        assertEquals(id, rev.getId());
        assertEquals(definition, rev.getDefinition());
    }

    @Test
    public void testMultiThreaded() {
        final XPathStaxParser parser = new XPathStaxParser();

        final AtomicReference<Book> ref1 = new AtomicReference<>();
        parser.addHandler(new NodeConverter<Book>("/catalog/book[@id='bk102']/*") {
            @Override
            public void process(Book object) {
                ref1.set(object);
            }
        });

        final AtomicReference<Book> ref2 = new AtomicReference<>();
        parser.addHandler(new NodeConverter<Book>("/catalog/book[@id='bk112']/*") {
            @Override
            public void process(Book object) {
                ref2.set(object);
            }
        });

        String[] fileNames = new String[]{"testxmls/books.xml", "testxmls/books2.xml"};
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch stopLatch = new CountDownLatch(fileNames.length);

        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (final String fileName : fileNames) {
            threadPool.submit(() -> {
                try {
                    startLatch.await();
                } catch (InterruptedException ignore) {
                }
                parser.parse(asStream(fileName));
                stopLatch.countDown();
            });
        }
        startLatch.countDown();

        try {
            stopLatch.await();
        } catch (InterruptedException e) {
            fail("failed to await termination");
        }

        assertBookDetails(ref1.get(), "bk102", "Ralls, Kim", "Midnight Rain", 5.95, Genre.Fantasy);
        assertBookDetails(ref2.get(), "bk112", "Galos, Mike", "Visual Studio 7: A Comprehensive Guide", 49.95,
            Genre.Computer);
    }

    private InputStream asStream(String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    private static String clean(String str) {
        return str.trim().replaceAll("\r", "").replaceAll("\n", "").replaceAll(" +", " ");
    }
}