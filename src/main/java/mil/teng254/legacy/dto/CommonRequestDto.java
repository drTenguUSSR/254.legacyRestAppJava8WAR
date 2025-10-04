package mil.teng254.legacy.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlRootElement
@XmlType(name = "RequestDto", propOrder = {
        "key", "stamp", "optionalField", "choiceField"
})
public class CommonRequestDto implements Serializable {
    private Long key;
    private String stamp;
    private String optionalField;
    private Object choiceField;

    public CommonRequestDto() {
    }

    // Геттеры и сеттеры
    @XmlElement(required = true)
    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    @XmlElement(required = true)
    public String getStamp() {
        return stamp;
    }

    public void setStamp(String stamp) {
        this.stamp = stamp;
    }

    @XmlElement(required = false, nillable = true)
    public String getOptionalField() {
        return optionalField;
    }

    public void setOptionalField(String optionalField) {
        this.optionalField = optionalField;
    }

    @XmlElements({
            @XmlElement(name = "integerValue", type = Integer.class),
            @XmlElement(name = "stringValue", type = String.class),
            @XmlElement(name = "booleanValue", type = Boolean.class)
    })
    public Object getChoiceField() {
        return choiceField;
    }

    public void setChoiceField(Object choiceField) {
        this.choiceField = choiceField;
    }
}
