package org.polyglotted.xpathstax.model;

import com.google.common.collect.ImmutableMap;
import org.polyglotted.xpathstax.api.AttributeProvider;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

@ThreadSafe
public class XPathRequest {
    public static final String SLASH = "/";
    private static final char SLASH_CHAR = '/';
    private static final char CURLY_BRACE_OPEN_CHAR = '{';
    private static final char CURLY_BRACE_CLOSE_CHAR = '}';

    private final String request;
    private final String elementName;
    private final boolean includeChildren;
    private final Map<String, XmlAttribute> attributesMap;

    public XPathRequest(String request) {
        this.request = checkNotNull(request);
        final String STAR = "*";

        StringBuilder builder = new StringBuilder();
        final Map<String, XmlAttribute> attribMap = newHashMap();

        List<String> values = new ArrayList<>();
        StringBuilder valueItemSb = new StringBuilder();

        // ignore characters within curly braces to handle xmlns naming.
        boolean isInCurlyBrace = false;
        for (int i = 0; i < this.request.length(); ++i) {
            char ch = this.request.charAt(i);

            if (ch == CURLY_BRACE_OPEN_CHAR)
                isInCurlyBrace = true;
            if (ch == CURLY_BRACE_CLOSE_CHAR)
                isInCurlyBrace = false;

            if (isInCurlyBrace) {
                valueItemSb.append(ch);
            }
            else {
                if (ch == SLASH_CHAR) {
                    if (!valueItemSb.toString().isEmpty()) {
                        values.add(valueItemSb.toString());
                        valueItemSb = new StringBuilder();
                    }
                }
                else if (i == this.request.length() - 1) {
                    valueItemSb.append(ch);
                    if (!valueItemSb.toString().isEmpty()) {
                        values.add(valueItemSb.toString());
                        valueItemSb = new StringBuilder();
                    }
                }
                else {
                    valueItemSb.append(ch);
                }
            }
        }

        for (String value : values) {
            final int elementEnd = getElementEnd(value);
            final String elementText = value.substring(0, elementEnd);

            if (!STAR.equals(elementText)) {
                builder.append(SLASH_CHAR);
                builder.append(elementText);
            }
            else {
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
        return request != null ? request.equals(other.request) : other.request == null;
    }

    @Override
    public int hashCode() {
        return 31 * ((request == null) ? 0 : request.hashCode());
    }

    public boolean canProcess(String elemName, XmlAttribute elementAttribute, AttributeProvider provider) {
        return (isElementEquals(elemName) || includeChildren &&
            elemName.startsWith(this.elementName)) && findParentAttributes(elementAttribute, provider);
    }

    public boolean isElementEquals(String elemName) {
        return checkNotNull(elemName, "elementname cannot be null").equals(this.elementName);
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
