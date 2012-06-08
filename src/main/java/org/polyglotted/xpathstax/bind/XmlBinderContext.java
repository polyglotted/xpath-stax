package org.polyglotted.xpathstax.bind;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static org.polyglotted.xpathstax.bind.ReflUtil.createNewData;
import static org.polyglotted.xpathstax.bind.ReflUtil.getAttributeName;
import static org.polyglotted.xpathstax.bind.ReflUtil.getElementName;
import static org.polyglotted.xpathstax.bind.ReflUtil.getRootElementName;
import static org.polyglotted.xpathstax.bind.ReflUtil.isBasicClass;
import static org.polyglotted.xpathstax.bind.ReflUtil.putPrimitiveCollection;
import static org.polyglotted.xpathstax.bind.ReflUtil.putPrimitiveValue;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;
import java.util.Stack;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.polyglotted.xpathstax.model.Value;
import org.polyglotted.xpathstax.model.XmlNode;

class XmlBinderContext<T> {

    private final Class<T> tClass;

    private final Map<String, Class<?>> types = newHashMap();

    private final Map<String, Field> primitiveAttributes = newHashMap();
    private final Map<String, Field> primitiveElements = newHashMap();
    private final Map<String, Field> primitiveCollection = newHashMap();

    private final Stack<Object> newObjects = new Stack<Object>();

    public XmlBinderContext(Class<T> tClass) {
        this.tClass = validate(tClass);
        build();
    }

    private void build() {
        types.put(getRootElementName(tClass), tClass);
        for (Field field : tClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(XmlAttribute.class)) {
                addPrimitiveAttribute(field);
                // TODO addPrimitiveEnum(field);

            } else if (field.isAnnotationPresent(XmlElement.class)) {
                if (Collection.class.isAssignableFrom(field.getType())) {
                    addCollectionElement(field);
                } else {
                    addPrimitiveElement(field);
                }
            }
        }
    }

    private void addCollectionElement(Field field) {
        ParameterizedType parametType = (ParameterizedType) field.getGenericType();
        Class<?> parametricClass = (Class<?>) parametType.getActualTypeArguments()[0];
        if (isBasicClass(parametricClass)) {
            field.setAccessible(true);
            primitiveCollection.put(getElementName(field), field);
        } else {
            System.out.println(field);
        }
    }

    private void addPrimitiveElement(Field field) {
        if (isBasicClass(field.getType())) {
            field.setAccessible(true);
            primitiveElements.put(getElementName(field), field);
        }
    }

    private void addPrimitiveAttribute(Field field) {
        if (isBasicClass(field.getType())) {
            field.setAccessible(true);
            primitiveAttributes.put(getAttributeName(field), field);
        }
    }

    private Class<T> validate(Class<T> tClass) {
        checkNotNull(tClass);
        checkArgument(tClass.isAnnotationPresent(XmlRootElement.class), tClass.getName() + " not a valid root element");
        return tClass;
    }

    public void elementStart(String element) {
        if (types.containsKey(element)) {
            newObjects.push(createNewData(types.get(element)));
        }
    }

    public T retrieve(XmlNode node) {
        Object lastObject = newObjects.pop();
        for (Map.Entry<String, Value> entry : node.getAttribute().iterate()) {
            if (primitiveAttributes.containsKey(entry.getKey())) {
                putPrimitiveValue(lastObject, entry.getValue(), primitiveAttributes.get(entry.getKey()));
            }
        }
        return tClass.cast(lastObject);
    }

    public void handleChildNode(XmlNode child) {
        Object lastObject = newObjects.peek();
        String name = child.getName();

        if (primitiveElements.containsKey(name)) {
            putPrimitiveValue(lastObject, child.getText(), primitiveElements.get(name));

        } else if (primitiveCollection.containsKey(name)) {
            putPrimitiveCollection(lastObject, child.getText(), primitiveCollection.get(name));
        }
    }
}
