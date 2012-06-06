package org.polyglotted.xpathstax.bind;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.polyglotted.xpathstax.model.Value;
import org.polyglotted.xpathstax.model.XmlNode;

class XmlBinderContext<T> {

    private static final String DEFAULT_NAME = "##default";
    private final Class<T> tClass;
    private final Map<String, Field> primitiveAttributes = newHashMap();
    private final Map<String, Field> primitiveElements = newHashMap();

    private T lastObject = null;

    public XmlBinderContext(Class<T> tClass) {
        this.tClass = validate(tClass);
        build();
    }

    private void build() {
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

    private String getElementName(Field field) {
        XmlElement element = (XmlElement) field.getAnnotation(XmlElement.class);
        return !DEFAULT_NAME.equals(element.name()) ? element.name() : field.getName();
    }

    private String getAttributeName(Field field) {
        XmlAttribute attribute = (XmlAttribute) field.getAnnotation(XmlAttribute.class);
        return !DEFAULT_NAME.equals(attribute.name()) ? attribute.name() : field.getName();
    }

    private boolean isBasicClass(Class<?> type) {
        return type.isPrimitive() || type.equals(String.class);
    }

    private Class<T> validate(Class<T> tClass) {
        checkNotNull(tClass);
        checkArgument(tClass.isAnnotationPresent(XmlRootElement.class), tClass.getName() + " not a valid root element");
        return tClass;
    }

    public void createNewData() {
        if (lastObject != null)
            return;

        try {
            lastObject = tClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public T retrieve() {
        T result = lastObject;
        lastObject = null;
        return result;
    }

    public void handleChildNode(XmlNode child) {
        if (primitiveElements.containsKey(child.getName())) {
            putPrimitiveText(child.getText(), primitiveElements.get(child.getName()));
        }
    }

    public void handleNode(XmlNode node) {
        for (Map.Entry<String, Value> entry : node.getAttribute().iterate()) {
            if (primitiveAttributes.containsKey(entry.getKey())) {
                putPrimitiveText(entry.getValue(), primitiveAttributes.get(entry.getKey()));
            }
        }
        //TODO set value text
    }

    private void putPrimitiveText(Value text, Field field) {
        checkNotNull(lastObject);
        try {
            field.set(lastObject, text.coerce(field.getType(), null));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String toString() {
        System.out.println(primitiveAttributes);
        System.out.println(primitiveElements);
        return "";
    }
}
