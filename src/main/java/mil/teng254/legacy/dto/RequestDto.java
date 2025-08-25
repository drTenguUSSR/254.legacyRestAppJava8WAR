package mil.teng254.legacy.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RequestDto {
    private Long key;
    private String stamp;

    // Геттеры и сеттеры
    public Long getKey() { return key; }
    public void setKey(Long key) { this.key = key; }

    public String getStamp() { return stamp; }
    public void setStamp(String stamp) { this.stamp = stamp; }
}


