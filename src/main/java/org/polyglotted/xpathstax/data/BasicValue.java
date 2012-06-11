package org.polyglotted.xpathstax.data;

import static java.text.DateFormat.getDateTimeInstance;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

public class BasicValue extends PrimitiveValue {

    public BasicValue(Object data) {
        super(data);
    }

    public BigDecimal asBigDecimal(BigDecimal defaultValue) {
        try {
            if (isNull()) {
                return defaultValue;

            } else if (data instanceof Number) {
                return BigDecimal.valueOf(((Number) data).doubleValue());
            }
            return new BigDecimal(asString(), MathContext.UNLIMITED);

        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public BigInteger asBigInteger(BigInteger defaultValue) {
        try {
            if (isNull()) {
                return defaultValue;

            } else if (data instanceof Number) {
                return BigInteger.valueOf(((Number) data).longValue());
            }
            return new BigInteger(asString());

        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public Date asDate(Date defaultValue) {
        return asDate(defaultValue, null);
    }

    public Date asDate(Date defaultValue, String datePattern) {
        if (isNull()) {
            return defaultValue;

        } else if (data instanceof Date) {
            return (Date) data;
        }
        try {
            DateFormat format = (datePattern == null) ? getDateTimeInstance() : new SimpleDateFormat(datePattern);
            return format.parse(asString());
        } catch (ParseException e) {
            return defaultValue;
        }
    }

    public <E extends Enum<E>> E asEnum(Class<E> clazz, E defaultValue) {
        if (isNull()) {
            return defaultValue;

        } else if (clazz.isAssignableFrom(data.getClass())) {
            return clazz.cast(data);
        }
        try {
            return Enum.valueOf(clazz, asString());
        } catch (Exception e) {
            return asXmlEnumValue(clazz, defaultValue);
        }
    }

    public <E extends Enum<E>> E asXmlEnumValue(Class<E> clazz, E defaultValue) {
        if (!clazz.isAnnotationPresent(XmlEnum.class)) {
            return defaultValue;
        }

        E result = null;
        for (Field fld : clazz.getDeclaredFields()) {
            if (fld.isEnumConstant() && fld.isAnnotationPresent(XmlEnumValue.class)) {
                String xmlValue = fld.getAnnotation(XmlEnumValue.class).value();
                if (xmlValue.equals(asString())) {
                    try {
                        result = clazz.cast(fld.get(null));
                    } catch (Exception e) {
                        //ignore
                    }
                    break;
                }
            }
        }
        return (result == null) ? defaultValue : result;
    }
}
