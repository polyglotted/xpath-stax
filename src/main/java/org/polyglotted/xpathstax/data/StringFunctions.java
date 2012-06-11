package org.polyglotted.xpathstax.data;

public class StringFunctions {

    private final Value value;
    
    public StringFunctions(Value value) {
        this.value = value;
    }
    
    public boolean isEmptyString() {
        return value.isNull() || "".equals(value.get());
    }

    public boolean isFilled() {
        return !isEmptyString();
    }

    public Value ignore(String... ignoredValues) {
        if (isEmptyString()) {
            return value;
        }
        String data = value.asString();
        for (String val : ignoredValues) {
            if (val.equals(data)) {
                return Value.of(null);
            }
        }
        return value;
    }

    public String left(int length) {
        String data = value.asString();
        if (length < 0) {
            length = length * -1;
            if (data.length() < length) {
                return "";
            }
            return data.substring(length);
        } else {
            if (data.length() < length) {
                return data;
            }
            return data.substring(0, length);
        }
    }

    public String right(int length) {
        String data = value.asString();
        if (length < 0) {
            length = length * -1;
            if (data.length() < length) {
                return data;
            }
            return data.substring(0, data.length() - length);
        } else {
            if (data.length() < length) {
                return data;
            }
            return data.substring(data.length() - length);
        }
    }

    public String substring(int start, int length) {
        String data = value.asString();
        if (start > data.length()) {
            return "";
        }
        return data.substring(start, Math.min(data.length(), length));
    }

    public int length() {
        return value.asString().length();
    }
}
