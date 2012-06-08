package org.polyglotted.xpathstax;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.polyglotted.xpathstax.model.XPathRequest.SLASH;

import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import org.polyglotted.xpathstax.api.AttributeProvider;
import org.polyglotted.xpathstax.api.NodeHandler;
import org.polyglotted.xpathstax.model.XmlAttribute;
import org.polyglotted.xpathstax.model.XmlNode;

import com.google.common.collect.Maps;

@NotThreadSafe
class NodeContext implements AttributeProvider {

    private final Map<String, NodeData> dataMap = Maps.newHashMap();

    @Override
    public XmlAttribute getAttribute(String attribElem) {
        NodeData data = dataMap.get(attribElem);
        return (data == null) ? XmlAttribute.EMPTY : data.node.getAttribute();
    }

    void addHandlers(String curElement, XmlAttribute attribute, List<NodeHandler> handlers) {
        dataMap.put(curElement, new NodeData(curElement, attribute, handlers));
    }

    void updateText(String curElement, String text) {
        NodeData data = checkNotNull(dataMap.get(curElement));
        data.node.setText(text);
    }

    void sendUpdates(String curElement) {
        NodeData data = checkNotNull(dataMap.remove(curElement));
        for (NodeHandler handler : data.handlers) {
            handler.processNode(data.node);
        }
    }

    private static class NodeData {
        private final XmlNode node;
        private final List<NodeHandler> handlers;

        public NodeData(String curElement, XmlAttribute attribute, List<NodeHandler> handlers) {
            this.node = new XmlNode(curElement, getName(curElement), attribute);
            this.handlers = handlers;
        }

        private String getName(String curElement) {
            return curElement.substring(curElement.lastIndexOf(SLASH) + 1);
        }
    }
}