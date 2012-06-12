package org.polyglotted.xpathstax.bind;

import static org.polyglotted.xpathstax.bind.ReflUtil.getParametricClass;

import javax.annotation.concurrent.NotThreadSafe;

import org.polyglotted.xpathstax.api.NodeHandler;
import org.polyglotted.xpathstax.model.XPathRequest;
import org.polyglotted.xpathstax.model.XmlNode;

/* experimental using javax.xml.bind annotations to simple objects */
@NotThreadSafe
public abstract class NodeConverter<T> implements NodeHandler {

    private final XmlBinderContext<T> context;
    private final XPathRequest request;

    public NodeConverter(String requestStr) {
        @SuppressWarnings("unchecked")
        Class<T> tClass = (Class<T>) getParametricClass(getClass());
        this.context = new XmlBinderContext<T>(tClass);
        this.request = new XPathRequest(requestStr);
    }

    public abstract void process(T object);

    @Override
    public final void elementStart(String elementName) {
        context.elementStart(elementName);
    }

    @Override
    public final void processNode(XmlNode node) {
        if (getRequest().isElementEquals(node.getPath())) {
            process(context.retrieve(node));
        } else {
            context.handleChildNode(node);
        }

    }

    public XPathRequest getRequest() {
        return request;
    }
}
