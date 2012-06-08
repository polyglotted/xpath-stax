package org.polyglotted.xpathstax.api;

import org.polyglotted.xpathstax.model.XmlNode;

public interface NodeHandler {

    void elementStart(String elementName);

    void processNode(XmlNode node);
}