package org.polyglotted.xpathstax.bind;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlType
public class Revision {
    
    @XmlEnum
    public enum Definition { Biblio, Collector }

    @XmlValue
    private String id;
    
    @XmlAttribute
    private int year;

    @XmlAttribute
    private Definition definition;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Definition getDefinition() {
        return definition;
    }

    public void setDefinition(Definition definition) {
        this.definition = definition;
    }
}
