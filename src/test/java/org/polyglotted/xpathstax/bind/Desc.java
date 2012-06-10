package org.polyglotted.xpathstax.bind;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlType
public class Desc {

    @XmlValue
    private String description;

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
