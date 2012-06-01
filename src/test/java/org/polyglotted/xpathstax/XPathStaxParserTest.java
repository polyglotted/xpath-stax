package org.polyglotted.xpathstax;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Test;
import org.polyglotted.xpathstax.api.NodeHandler;
import org.polyglotted.xpathstax.model.Value;
import org.polyglotted.xpathstax.model.XPathRequest;
import org.polyglotted.xpathstax.model.XmlNode;

import com.google.common.util.concurrent.AtomicDouble;

public class XPathStaxParserTest {

    @Test
    public void testParse() {

        XPathStaxParser parser = new XPathStaxParser();

        final AtomicDouble resultCount = new AtomicDouble();
        parser.addHandler(new XPathRequest("/catalog/book[@id='bk101']/price"), new NodeHandler() {
            @Override
            public void process(XmlNode xmlNode) {
                Value count = xmlNode.getText();
                resultCount.set(count.asDouble(0));
            }
        });

        parser.parse(asStream("testxmls/books.xml"));

        assertEquals(44.95, resultCount.get(), 0.001);
    }

    // TODO multi-thread test

    protected InputStream asStream(String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
    }
}