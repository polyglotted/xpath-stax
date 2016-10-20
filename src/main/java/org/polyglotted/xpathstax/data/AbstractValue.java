package org.polyglotted.xpathstax.data;

import java.util.regex.Pattern;

@SuppressWarnings({"WeakerAccess", "unused"})
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
    public int hashCode() {
        return 31 * ((data == null) ? 0 : data.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        AbstractValue other = (AbstractValue) obj;
        return data != null ? data.equals(other.data) : other.data == null;
    }

    @Override
    public String toString() {
        return asString(null);
    }
}
