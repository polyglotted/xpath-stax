/**
 * Copyright (c) 2012 scireum GmbH - Andreas Haufler - aha@scireum.de
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * https://github.com/andyHa/scireumOpen/blob/master/src/com/scireum/open/commons/Value.java
 */
package org.polyglotted.xpathstax.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.regex.Pattern;

public class Value {

    private Object data;

    public boolean isNull() {
        return data == null;
    }

    public boolean isEmptyString() {
        return data == null || "".equals(data);
    }

    public boolean isFilled() {
        return !isEmptyString();
    }

    public Value ignore(String... ignoredValues) {
        if (isEmptyString()) {
            return this;
        }
        for (String val : ignoredValues) {
            if (data.equals(val)) {
                return Value.of(null);
            }
        }
        return this;
    }

    private static final Pattern NUMBER = Pattern.compile("\\d+(\\.\\d+)?");

    public boolean isNumeric() {
        return data != null && data instanceof Number
                || NUMBER.matcher(asString("")).matches();
    }

    public Object get() {
        return data;
    }

    public Object get(Object defaultValue) {
        return data == null ? defaultValue : data;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> T coerce(Class<?> targetClazz, T defaultValue) {
        if (data == null) {
            return null;
        }
        if (targetClazz.isAssignableFrom(data.getClass())) {
            return (T) data;
        }
        if (String.class.equals(targetClazz)) {
            return (T) getString();
        }
        if (Integer.class.equals(targetClazz) || int.class.equals(targetClazz)) {
            return (T) getInteger();
        }
        if (Long.class.equals(targetClazz) || long.class.equals(targetClazz)) {
            return (T) getLong();
        }
        if (Boolean.class.equals(targetClazz)
                || boolean.class.equals(targetClazz)) {
            return (T) (Boolean) Boolean.parseBoolean(String.valueOf(data));
        }
        if (BigDecimal.class.equals(targetClazz)) {
            return (T) getBigDecimal(null);
        }
        if (targetClazz.isEnum()) {
            try {
                return (T) Enum
                        .valueOf((Class<Enum>) targetClazz, asString(""));
            } catch (Exception e) {
                return (T) Enum.valueOf((Class<Enum>) targetClazz, asString("")
                        .toUpperCase());
            }
        }
        throw new IllegalArgumentException("Cannot convert to: " + targetClazz);
    }

    @SuppressWarnings("unchecked")
    public <V> V get(Class<V> clazz, V defaultValue) {
        Object result = get(defaultValue);
        if (result == null || !clazz.isAssignableFrom(result.getClass())) {
            return defaultValue;
        }
        return (V) result;
    }

    public String getString() {
        return isNull() ? null : asString();
    }

    public String asString() {
        return data == null ? "" : data.toString();
    }

    public String asString(String defaultValue) {
        return isNull() ? defaultValue : asString();
    }

    public boolean asBoolean(boolean defaultValue) {
        if (isNull()) {
            return defaultValue;
        }
        if (data instanceof Boolean) {
            return (Boolean) data;
        }
        return Boolean.parseBoolean(String.valueOf(data));
    }

    public boolean asBoolean() {
        return asBoolean(false);
    }

    public int asInt(int defaultValue) {
        try {
            if (isNull()) {
                return defaultValue;
            }
            if (data instanceof Integer) {
                return (Integer) data;
            }
            return Integer.parseInt(String.valueOf(data));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public Integer getInteger() {
        try {
            if (isNull()) {
                return null;
            }
            if (data instanceof Integer) {
                return (Integer) data;
            }
            return Integer.parseInt(String.valueOf(data));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public long asLong(long defaultValue) {
        try {
            if (isNull()) {
                return defaultValue;
            }
            if (data instanceof Long) {
                return (Long) data;
            }
            if (data instanceof Integer) {
                return (Integer) data;
            }
            return Long.parseLong(String.valueOf(data));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public Long getLong() {
        try {
            if (isNull()) {
                return null;
            }
            if (data instanceof Long) {
                return (Long) data;
            }
            return Long.parseLong(String.valueOf(data));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public double asDouble(double defaultValue) {
        try {
            if (isNull()) {
                return defaultValue;
            }
            if (data instanceof Double) {
                return (Double) data;
            }
            if (data instanceof Long) {
                return (Long) data;
            }
            if (data instanceof Integer) {
                return (Integer) data;
            }
            return Double.parseDouble(String.valueOf(data));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public BigDecimal getBigDecimal(BigDecimal defaultValue) {
        try {
            if (isNull()) {
                return defaultValue;
            }
            if (data instanceof Double) {
                return BigDecimal.valueOf((Double) data);
            }
            if (data instanceof Long) {
                return BigDecimal.valueOf((Long) data);
            }
            if (data instanceof Integer) {
                return BigDecimal.valueOf((Integer) data);
            }
            if (data instanceof Long) {
                return BigDecimal.valueOf((Long) data);
            }
            return new BigDecimal(asString("").replace(",", "."),
                    MathContext.UNLIMITED);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Value of(Object data) {
        Value val = new Value();
        val.data = data;
        return val;
    }

    @Override
    public String toString() {
        return asString();
    }

    @SuppressWarnings("unchecked")
    public <E extends Enum<E>> E asEnum(Class<E> clazz) {
        if (data == null) {
            return null;
        }
        if (clazz.isAssignableFrom(data.getClass())) {
            return (E) data;
        }
        try {
            return Enum.valueOf(clazz, String.valueOf(data));
        } catch (Exception e) {
            return null;
        }
    }

    public String left(int length) {
        String value = asString();
        if (value == null) {
            return null;
        }
        if (length < 0) {
            length = length * -1;
            if (value.length() < length) {
                return "";
            }
            return value.substring(length);
        } else {
            if (value.length() < length) {
                return value;
            }
            return value.substring(0, length);
        }
    }

    public String right(int length) {
        String value = asString();
        if (value == null) {
            return null;
        }
        if (length < 0) {
            length = length * -1;
            if (value.length() < length) {
                return value;
            }
            return value.substring(0, value.length() - length);
        } else {
            if (value.length() < length) {
                return value;
            }
            return value.substring(value.length() - length);
        }
    }

    public String substring(int start, int length) {
        String value = asString();
        if (value == null) {
            return null;
        }
        if (start > value.length()) {
            return "";
        }
        return value.substring(start, Math.min(value.length(), length));
    }

    public int length() {
        String value = asString();
        if (value == null) {
            return 0;
        }
        return value.length();
    }

    public boolean is(Class<?> clazz) {
        return get() != null && clazz.isAssignableFrom(get().getClass());
    }
}