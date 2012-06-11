package org.polyglotted.xpathstax.model;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.concurrent.ThreadSafe;

import org.polyglotted.xpathstax.data.Value;

@ThreadSafe
public class XmlNode {

    private final String path;
    private final String name;
    private final XmlAttribute attribute;
    private String text;

    public XmlNode(String path, String name, XmlAttribute attribute) {
        this.path = checkNotNull(path, "Full path cannot be null");
        this.name = checkNotNull(name, "XmlNode name cannot be null");
        this.attribute = checkNotNull(attribute, "XmlAttribute cannot be null");
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public XmlAttribute getAttribute() {
        return attribute;
    }

    public Value getText() {
        return Value.of(text);
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        XmlNode other = (XmlNode) obj;
        if (path != null ? !path.equals(other.path) : other.path != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return 31 * 1 + ((path == null) ? 0 : path.hashCode());
    }

    @Override
    public String toString() {
        return path;
    }
}
