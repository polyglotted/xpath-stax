package org.polyglotted.xpathstax.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@SuppressWarnings("WeakerAccess")
public final class Value extends BasicValue {

    public Value(Object data) {
        super(data);
    }

    public static Value of(Object data) {
        return new Value(data);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> T coerce(Class<? extends T> targetClazz, T defaultValue) {
        if (isNull()) {
            return defaultValue;
        }
        else if (instanceOf(targetClazz)) {
            return targetClazz.cast(get());
        }
        else if (String.class.equals(targetClazz)) {
            return (T) asString();
        }
        else if (Boolean.class.equals(targetClazz) || boolean.class.equals(targetClazz)) {
            return (T) asBoolean((Boolean) defaultValue);
        }
        else if (Character.class.equals(targetClazz) || char.class.equals(targetClazz)) {
            return (T) asCharacter((Character) defaultValue);
        }
        else if (Byte.class.equals(targetClazz) || byte.class.equals(targetClazz)) {
            return (T) asByte((Byte) defaultValue);
        }
        else if (Short.class.equals(targetClazz) || short.class.equals(targetClazz)) {
            return (T) asShort((Short) defaultValue);
        }
        else if (Integer.class.equals(targetClazz) || int.class.equals(targetClazz)) {
            return (T) asInteger((Integer) defaultValue);
        }
        else if (Long.class.equals(targetClazz) || long.class.equals(targetClazz)) {
            return (T) asLong((Long) defaultValue);
        }
        else if (Float.class.equals(targetClazz) || float.class.equals(targetClazz)) {
            return (T) asFloat((Float) defaultValue);
        }
        else if (Double.class.equals(targetClazz) || double.class.equals(targetClazz)) {
            return (T) asDouble((Double) defaultValue);
        }
        else if (BigDecimal.class.equals(targetClazz)) {
            return (T) asBigDecimal((BigDecimal) defaultValue);
        }
        else if (BigInteger.class.equals(targetClazz)) {
            return (T) asBigInteger((BigInteger) defaultValue);
        }
        else if (Date.class.equals(targetClazz)) {
            return (T) asDate((Date) defaultValue);
        }
        else if (targetClazz.isEnum()) {
            return (T) asEnum((Class<Enum>) targetClazz, null); // TODO fix me
        }
        throw new IllegalArgumentException("Cannot convert to: " + targetClazz);
    }
}