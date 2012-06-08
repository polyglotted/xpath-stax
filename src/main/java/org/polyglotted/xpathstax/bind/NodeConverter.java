package org.polyglotted.xpathstax.bind;

import java.lang.reflect.ParameterizedType;

import org.polyglotted.xpathstax.api.NodeHandler;
import org.polyglotted.xpathstax.model.XPathRequest;
import org.polyglotted.xpathstax.model.XmlNode;

/* experimental using javax.xml.bind annotations to simple objects */
public abstract class NodeConverter<T> implements NodeHandler {

    private final XmlBinderContext<T> context;
    private final XPathRequest request;

    public NodeConverter(String requestStr) {
        this.context = new XmlBinderContext<T>(getParametricClass());
        this.request = new XPathRequest(requestStr);
    }

    private Class<T> getParametricClass() {
        ParameterizedType parametType = (ParameterizedType) getClass().getGenericSuperclass();
        @SuppressWarnings("unchecked")
        Class<T> parametricClass = (Class<T>) parametType.getActualTypeArguments()[0];
        return parametricClass;
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
