package org.polyglotted.xpathstax.bind;

import com.google.common.annotations.VisibleForTesting;
import org.polyglotted.xpathstax.data.Value;
import org.polyglotted.xpathstax.model.XmlNode;

import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.annotation.*;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Stack;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newHashMap;
import static org.polyglotted.xpathstax.bind.ReflUtil.*;

@SuppressWarnings("WeakerAccess")
@NotThreadSafe
class XmlBinderContext<T> {

    private final Class<T> rootClass;
    private final String rootName;
    private final Map<String, Class<?>> types = newHashMap();
    private final Map<Class<?>, ClassContext> contexts = newHashMap();

    private final Stack<String> nameStack = new Stack<>();
    private final Stack<Object> newObjects = new Stack<>();

    public XmlBinderContext(Class<T> tClass) {
        rootClass = validateRoot(tClass);
        rootName = getRootElementName(rootClass);

        build(rootName, rootClass);
    }

    private void build(String fieldName, Class<?> clazz) {
        final ClassContext ctx = new ClassContext();
        types.put(fieldName, clazz);
        contexts.put(clazz, ctx);

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(XmlValue.class)) {
                checkBasicClass(field.getType(), field.getName());
                ctx.setXmlValueField(field);

            } else if (field.isAnnotationPresent(XmlAttribute.class)) {
                if (!isEnumType(field.getType()))
                    checkBasicClass(field.getType(), field.getName());
                ctx.addAttribute(field);

            } else if (field.isAnnotationPresent(XmlElement.class)) {
                Class<?> fieldClass = getFieldClass(field);

                if (isXmlType(fieldClass)) {
                    build(getElementName(field), fieldClass);

                } else {
                    if (!isEnumType(fieldClass))
                        checkBasicClass(fieldClass, field.getName());
                }

                if (isCollection(field)) {
                    ctx.addCollection(field);
                } else {
                    ctx.addElement(field);
                }
            }
        }
    }

    private void checkBasicClass(Class<?> clazz, String fieldName) {
        checkArgument(isBasicClass(clazz), fieldName + " is not a primitive or String");
    }

    private boolean isXmlType(Class<?> fieldClass) {
        return fieldClass.isAnnotationPresent(XmlType.class);
    }

    private boolean isEnumType(Class<?> fieldClass) {
        return fieldClass.isEnum() && fieldClass.isAnnotationPresent(XmlEnum.class);
    }

    public void elementStart(String element) {
        if (types.containsKey(element)) {
            nameStack.push(element);
            newObjects.push(createNewData(types.get(element)));
        }
    }

    public T retrieve(XmlNode node) {
        checkArgument(rootName.equals(node.getName()));

        Object rootObject = newObjects.pop();
        setAttributesAndXmlValue(rootObject, node, contexts.get(rootClass));

        return rootClass.cast(rootObject);
    }

    public void handleChildNode(XmlNode child) {
        String childName = child.getName();
        Object childObj = null;

        if (types.containsKey(childName)) {
            checkArgument(childName.equals(nameStack.pop()));
            childObj = newObjects.pop();
            setAttributesAndXmlValue(childObj, child, contexts.get(types.get(childName)));
        }

        ClassContext prevCtx = contexts.get(types.get(nameStack.peek()));
        Object prevObject = newObjects.peek();

        if (childObj == null) {
            prevCtx.putValue(prevObject, childName, child.getText());
        } else {
            prevCtx.putChild(prevObject, childName, childObj);
        }
    }

    private void setAttributesAndXmlValue(Object object, XmlNode node, ClassContext context) {
        context.putXmlValue(object, node.getText());
        for (Map.Entry<String, Value> entry : node.getAttribute().iterate()) {
            context.putValue(object, entry.getKey(), entry.getValue());
        }
    }

    @VisibleForTesting
    Map<String, Class<?>> getTypes() {
        return types;
    }

    @VisibleForTesting
    Map<Class<?>, ClassContext> getContexts() {
        return contexts;
    }

    @VisibleForTesting
    static class ClassContext {
        final Map<String, Field> attributes = newHashMap();
        final Map<String, Field> elements = newHashMap();
        final Map<String, Field> collections = newHashMap();
        private Field xmlValueField = null;

        void addAttribute(Field field) {
            field.setAccessible(true);
            attributes.put(getAttributeName(field), field);
        }

        void addElement(Field field) {
            field.setAccessible(true);
            elements.put(getElementName(field), field);
        }

        void addCollection(Field field) {
            field.setAccessible(true);
            collections.put(getElementName(field), field);
        }

        void putValue(Object lastObject, String fieldName, Value value) {
            if (elements.containsKey(fieldName)) {
                putPrimitiveValue(lastObject, value, elements.get(fieldName));

            } else if (collections.containsKey(fieldName)) {
                putPrimitiveInCollection(lastObject, value, collections.get(fieldName));

            } else if (attributes.containsKey(fieldName)) {
                putPrimitiveValue(lastObject, value, attributes.get(fieldName));
            }
        }

        void putChild(Object lastObject, String fieldName, Object child) {
            if (elements.containsKey(fieldName)) {
                putChildObject(lastObject, child, elements.get(fieldName));

            } else if (collections.containsKey(fieldName)) {
                putChildInCollection(lastObject, child, collections.get(fieldName));
            }
        }

        void putXmlValue(Object object, Value value) {
            if (xmlValueField != null)
                putPrimitiveValue(object, value, xmlValueField);
        }

        void setXmlValueField(Field field) {
            field.setAccessible(true);
            this.xmlValueField = field;
        }

        Field getXmlValueField() {
            return xmlValueField;
        }
    }
}
