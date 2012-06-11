package org.polyglotted.xpathstax.bind;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.polyglotted.xpathstax.data.Value;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

class ReflUtil {
    private static final String DEFAULT_NAME = "##default";

    static <M> Class<M> validateRoot(Class<M> tClass) {
        checkNotNull(tClass);
        checkArgument(tClass.isAnnotationPresent(XmlRootElement.class), tClass.getName() + " not a valid root element");
        return tClass;
    }

    static boolean isBasicClass(Class<?> type) {
        return type.isPrimitive() || type.equals(String.class);
    }

    static boolean isCollection(Field field) {
        return Collection.class.isAssignableFrom(field.getType());
    }

    static Class<?> getParametricClass(Class<?> clazz) {
        ParameterizedType paramType = (ParameterizedType) clazz.getGenericSuperclass();
        return getParametricClass(paramType);
    }

    static Class<?> getParametricClass(Field field) {
        ParameterizedType paramType = (ParameterizedType) field.getGenericType();
        return getParametricClass(paramType);
    }

    private static Class<?> getParametricClass(ParameterizedType parametType) {
        return (Class<?>) parametType.getActualTypeArguments()[0];
    }

    static String getRootElementName(Class<?> clazz) {
        XmlRootElement element = (XmlRootElement) clazz.getAnnotation(XmlRootElement.class);
        return !DEFAULT_NAME.equals(element.name()) ? element.name() : clazz.getSimpleName();
    }

    static String getElementName(Field field) {
        XmlElement element = (XmlElement) field.getAnnotation(XmlElement.class);
        return !DEFAULT_NAME.equals(element.name()) ? element.name() : field.getName();
    }

    static String getAttributeName(Field field) {
        XmlAttribute attribute = (XmlAttribute) field.getAnnotation(XmlAttribute.class);
        return !DEFAULT_NAME.equals(attribute.name()) ? attribute.name() : field.getName();
    }

    static <T> T createNewData(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    static void putPrimitiveValue(Object lastObject, Value value, Field field) {
        checkNotNull(field);
        try {
            field.set(checkNotNull(lastObject), value.coerce(field.getType(), null));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    static void putChildObject(Object lastObject, Object child, Field field) {
        try {
            checkNotNull(field).set(checkNotNull(lastObject), child);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    static void putPrimitiveInCollection(Object lastObject, Value value, Field field) {
        try {
            getOrCreateColl(checkNotNull(lastObject), checkNotNull(field)).add(value.get());

        } catch (RuntimeException re) {
            throw re;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    static void putChildInCollection(Object lastObject, Object child, Field field) {
        try {
            getOrCreateColl(checkNotNull(lastObject), checkNotNull(field)).add(child);

        } catch (RuntimeException re) {
            throw re;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Collection<Object> getOrCreateColl(Object lastObject, Field field) throws Exception {
        @SuppressWarnings("unchecked")
        Collection<Object> coll = (Collection<Object>) (field.get(lastObject));
        if (coll == null) {
            coll = createColl(lastObject, field);
            field.set(lastObject, coll);
        }
        return coll;
    }
    
    private static Collection<Object> createColl(Object lastObject, Field field) {
        Class<?> type = field.getType();
        if (type.isAssignableFrom(ArrayList.class)) {
            return Lists.newArrayList();
        } else if (type.isAssignableFrom(HashSet.class)) {
            return Sets.newHashSet();
        }
        throw new IllegalStateException("currently supports only lists and sets");
    }
}
