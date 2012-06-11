package org.polyglotted.xpathstax.data;

public class PrimitiveValue extends AbstractValue {

    public PrimitiveValue(Object data) {
        super(data);
    }

    public boolean asBoolean() {
        return asBoolean(false);
    }

    public Boolean asBoolean(Boolean defaultValue) {
        if (isNull()) {
            return defaultValue;

        } else if (data instanceof Boolean) {
            return ((Boolean) data).booleanValue();
        }
        return Boolean.parseBoolean(asString());
    }

    public char asChar() {
        return asCharacter((char) 0);
    }

    public Character asCharacter(Character defaultValue) {
        if (isNull()) {
            return defaultValue;

        } else if (data instanceof Character) {
            return ((Character) data).charValue();

        } else if (data instanceof Byte) {
            return (char) ((Byte) data).byteValue();
        }
        throw new UnsupportedOperationException(asString() + " cannot be converted to char");
    }

    public byte asByte() {
        return asByte((byte) 0);
    }

    public Byte asByte(Byte defaultValue) {
        if (isNull()) {
            return defaultValue;

        } else if (data instanceof Number) {
            return ((Number) data).byteValue();
        }
        try {
            return Byte.parseByte(asString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public short asShort() {
        return asShort((short) 0);
    }

    public Short asShort(Short defaultValue) {
        if (isNull()) {
            return defaultValue;

        } else if (data instanceof Number) {
            return ((Number) data).shortValue();
        }
        try {
            return Short.parseShort(asString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public int asInt() {
        return asInteger(0);
    }

    public Integer asInteger(Integer defaultValue) {
        if (isNull()) {
            return defaultValue;

        } else if (data instanceof Number) {
            return ((Number) data).intValue();
        }
        try {
            return Integer.parseInt(asString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public long asLong() {
        return asLong(0L);
    }

    public Long asLong(Long defaultValue) {
        if (isNull()) {
            return defaultValue;

        } else if (data instanceof Number) {
            return ((Number) data).longValue();
        }
        try {
            return Long.parseLong(asString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public float asFloat() {
        return asFloat(0.0f);
    }

    public Float asFloat(Float defaultValue) {
        if (isNull()) {
            return defaultValue;

        } else if (data instanceof Number) {
            return ((Number) data).floatValue();
        }
        try {
            return Float.parseFloat(asString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public double asDouble() {
        return asDouble(0.0d);
    }

    public Double asDouble(Double defaultValue) {
        if (isNull()) {
            return defaultValue;

        } else if (data instanceof Number) {
            return ((Number) data).doubleValue();
        }
        try {
            return Double.parseDouble(asString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
