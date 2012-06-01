package org.polyglotted.xpathstax.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.ThreadSafe;

import org.polyglotted.xpathstax.api.AttributeProvider;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;

@ThreadSafe
public class XPathRequest {

    public static final String SLASH = "/";

    private static final Splitter SLASH_SPLITTER = Splitter.on(SLASH).trimResults().omitEmptyStrings();

    private final String request;
    private final String elementName;
    private final boolean includeChildren;
    private final Map<String, XmlAttribute> attributesMap;

    public XPathRequest(String request) {
        this.request = checkNotNull(request);
        final String STAR = "*";

        StringBuilder builder = new StringBuilder();
        final Map<String, XmlAttribute> attribMap = newHashMap();

        Iterable<String> values = SLASH_SPLITTER.split(this.request);
        for (String value : values) {
            final int elementEnd = getElementEnd(value);
            final String elementText = value.substring(0, elementEnd);

            if (!STAR.equals(elementText)) {
                builder.append(SLASH);
                builder.append(elementText);
            } else {
                checkArgument(request.endsWith(STAR), "* can only be the last char in the request");
            }

            if (elementEnd != value.length()) {
                attribMap.put(builder.toString(), parseAttribute(value.substring(elementEnd)));
            }
        }

        this.elementName = builder.toString();
        this.includeChildren = request.endsWith(STAR);
        this.attributesMap = ImmutableMap.copyOf(attribMap);
    }

    private XmlAttribute parseAttribute(String value) {
        checkArgument(value.startsWith("[@"), "support only string predicate, given text " + value);
        checkArgument(value.endsWith("]"), "malformed predicate text, given text " + value);
        return XmlAttribute.from(value.substring(2, value.length() - 1));
    }

    private int getElementEnd(String value) {
        int bIndex = value.indexOf('[');
        return bIndex > 0 ? bIndex : value.length();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        XPathRequest other = (XPathRequest) obj;
        if (request != null ? !request.equals(other.request) : other.request != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return 31 * 1 + ((request == null) ? 0 : request.hashCode());
    }

    public boolean canProcess(String elemName, XmlAttribute elementAttribute, AttributeProvider provider) {
        checkNotNull(elemName, "elementname cannot be null");
        if (elemName.equals(this.elementName) || includeChildren && elemName.startsWith(this.elementName)) {
            return findParentAttributes(elementAttribute, provider);
        }

        return false;
    }

    private boolean findParentAttributes(XmlAttribute elementAttribute, AttributeProvider provider) {
        boolean result = true;
        for (Entry<String, XmlAttribute> entry : attributesMap.entrySet()) {
            XmlAttribute parentAttrib = provider.getAttribute(entry.getKey());
            if (!elementAttribute.contains(entry.getValue()) && !parentAttrib.contains(entry.getValue())) {
                result = false;
                break;
            }
        }
        return result;
    }
}