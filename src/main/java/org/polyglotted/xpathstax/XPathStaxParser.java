package org.polyglotted.xpathstax;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.polyglotted.xpathstax.model.XPathRequest.SLASH;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.events.XMLEvent;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.polyglotted.xpathstax.api.NodeHandler;
import org.polyglotted.xpathstax.bind.NodeConverter;
import org.polyglotted.xpathstax.model.XPathRequest;
import org.polyglotted.xpathstax.model.XmlAttribute;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

public class XPathStaxParser {

    private static final XMLInputFactory2 factory = createFactory();

    private final Map<XPathRequest, NodeHandler> handlersMap = Maps.newConcurrentMap();

    public void addHandler(XPathRequest request, NodeHandler handler) {
        handlersMap.put(checkNotNull(request), checkNotNull(handler));
    }

    public <T> void addHandler(NodeConverter<T> converter) {
        handlersMap.put(checkNotNull(converter.getRequest()), checkNotNull(converter));
    }

    public void parse(InputStream inputStream) {
        final NodeContext context = new NodeContext();
        try {
            XMLStreamReader2 xmlr = (XMLStreamReader2) factory.createXMLStreamReader(inputStream);
            String curElement = "";
            int eventType = xmlr.getEventType();
            while (xmlr.hasNext()) {
                eventType = xmlr.next();

                switch (eventType) {
                case XMLEvent.START_ELEMENT:
                    curElement = curElement + SLASH + xmlr.getName().toString();
                    processStartElement(xmlr, context, curElement);
                    break;

                case XMLEvent.CHARACTERS:
                    context.updateText(curElement,
                                    new String(xmlr.getTextCharacters(), xmlr.getTextStart(), xmlr.getTextLength()));
                    break;

                case XMLEvent.END_ELEMENT:
                    if (!SLASH.equals(curElement)) {
                        context.sendUpdates(curElement);
                    }
                    curElement = curElement.substring(0, curElement.lastIndexOf(SLASH + xmlr.getName().toString()));
                    break;
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("parse failed", ex);

        } finally {
            Closeables.closeQuietly(inputStream);
        }
    }

    private void processStartElement(XMLStreamReader2 xmlr, NodeContext context, String curElement) {
        XmlAttribute attribute = XmlAttribute.from(xmlr);

        List<NodeHandler> handlers = Lists.newArrayList();
        for (Entry<XPathRequest, NodeHandler> entry : handlersMap.entrySet()) {
            if (entry.getKey().canProcess(curElement, attribute, context)) {
                handlers.add(entry.getValue());
            }
        }

        context.addHandlers(curElement, attribute, handlers);
    }

    private static XMLInputFactory2 createFactory() {
        XMLInputFactory2 xmlif = (XMLInputFactory2) XMLInputFactory2.newInstance();
        xmlif.configureForConvenience();
        return xmlif;
    }
}