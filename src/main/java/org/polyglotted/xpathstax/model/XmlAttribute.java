package org.polyglotted.xpathstax.model;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.codehaus.stax2.XMLStreamReader2;
import org.polyglotted.xpathstax.data.Value;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("WeakerAccess")
@ThreadSafe
public class XmlAttribute {

    private static final String NP_SPACE = String.valueOf((char) 22);
    private static final String EQUALS = "=";
    private static final Splitter SPACE_SPLITTER = Splitter.on(" ").trimResults().omitEmptyStrings();
    private static final Splitter NPSPACE_SPLITTER = Splitter.on(NP_SPACE).trimResults().omitEmptyStrings();
    private static final Splitter EQUALS_SPLITTER = Splitter.on(EQUALS).trimResults().omitEmptyStrings();

    public static final XmlAttribute EMPTY = XmlAttribute.from("");

    private final StringBuffer buffer = new StringBuffer();
    private AtomicInteger count = new AtomicInteger(0);

    public static XmlAttribute from(String attributeString) {
        XmlAttribute attr = new XmlAttribute();

        Iterable<String> attributes = SPACE_SPLITTER.split(attributeString);
        for (String value : attributes) {
            Iterator<String> iter = splitByEquals(value);
            attr.add(iter.next(), iter.hasNext() ? iter.next() : "");
        }
        return attr;
    }

    public static XmlAttribute from(XMLStreamReader2 xmlr) {
        XmlAttribute attr = new XmlAttribute();

        for (int i = 0; i < xmlr.getAttributeCount(); i++) {
            attr.add(xmlr.getAttributeLocalName(i), xmlr.getAttributeValue(i));
        }
        return attr;
    }

    public void add(String name, String value) {
        checkArgument(!name.contains(EQUALS));
        buffer.append(buildKey(name));
        buffer.append(buildValue(value));
        count.incrementAndGet();
    }

    public int count() {
        return count.get();
    }

    public boolean contains(String name) {
        return buffer.indexOf(buildKey(name)) >= 0;
    }

    public boolean contains(String name, String value) {
        return buffer.indexOf(buildKey(name) + buildValue(value)) >= 0;
    }

    public boolean contains(XmlAttribute inner) {
        if (inner == null)
            return false;

        if (inner == this)
            return true;

        if (inner.count() == 1) {
            return buffer.indexOf(inner.buffer.toString()) >= 0;
        }

        boolean result = true;
        for (String part : NPSPACE_SPLITTER.split(inner.buffer)) {
            if (buffer.indexOf(NP_SPACE + part) < 0) {
                result = false;
                break;
            }
        }
        return result;
    }

    public Value get(String name) {
        String result = null;
        final String key = buildKey(name);
        int keyIndex = buffer.indexOf(key);
        if (keyIndex >= 0) {
            int fromIndex = keyIndex + key.length();
            int lastIndex = buffer.indexOf(NP_SPACE, fromIndex);
            result = (lastIndex >= 0) ? buffer.substring(fromIndex, lastIndex) : buffer.substring(fromIndex);
        }

        return Value.of(result);
    }

    public Iterable<Entry<String, Value>> iterate() {
        return Iterables.transform(NPSPACE_SPLITTER.split(buffer), AttrEntry::new);
    }

    @Override
    public String toString() {
        return buffer.toString();
    }

    private static String buildKey(String name) {
        return NP_SPACE + checkNotNull(name) + EQUALS;
    }

    private static String buildValue(String value) {
        return checkNotNull(value).replaceAll("'", "").replaceAll("\"", "");
    }

    private static Iterator<String> splitByEquals(String value) {
        Iterator<String> iter = EQUALS_SPLITTER.split(value).iterator();
        checkArgument(iter.hasNext(), "unable to parse attribute " + value);
        return iter;
    }

    private static class AttrEntry implements Entry<String, Value> {

        private final String key;
        private final Value value;

        AttrEntry(String data) {
            Iterator<String> iter = splitByEquals(data);
            this.key = iter.next();
            this.value = iter.hasNext() ? Value.of(iter.next()) : Value.of(null);
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Value getValue() {
            return value;
        }

        @Override
        public Value setValue(Value value) {
            throw new UnsupportedOperationException();
        }
    }
}
