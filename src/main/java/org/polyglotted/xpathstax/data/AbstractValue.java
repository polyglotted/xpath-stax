package org.polyglotted.xpathstax.data;

import java.util.regex.Pattern;

abstract class AbstractValue {

    private static final Pattern NUMBER = Pattern.compile("\\d+(\\.\\d+)?");
    protected final Object data;

    protected AbstractValue(Object data) {
        this.data = data;
    }

    public boolean isNull() {
        return data == null;
    }

    public boolean isNumeric() {
        return (data instanceof Number) || NUMBER.matcher(asString("")).matches();
    }

    public boolean instanceOf(Class<?> clazz) {
        return (data != null) && clazz.isAssignableFrom(data.getClass());
    }

    public Object get() {
        return data;
    }

    public Object get(Object defaultValue) {
        return data == null ? defaultValue : data;
    }

    public <V> V get(Class<V> clazz, V defaultValue) {
        return instanceOf(clazz) ? clazz.cast(data) : defaultValue;
    }

    public String asString() {
        return asString("");
    }

    public String asString(String defaultValue) {
        return isNull() ? defaultValue : String.valueOf(data);
    }

    @Override
    public String toString() {
        return asString(null);
    }
}
