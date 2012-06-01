package org.polyglotted.xpathstax.api;

import org.polyglotted.xpathstax.model.XmlAttribute;

public interface AttributeProvider {

    XmlAttribute getAttribute(String attribElem);
}