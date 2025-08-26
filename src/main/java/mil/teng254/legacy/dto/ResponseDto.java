package mil.teng254.legacy.dto;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class ResponseDto implements Serializable {
    private Long res;
    private String stamp;
    private String tz;

    // Геттеры и сеттеры
    public Long getRes() { return res; }
    public void setRes(Long res) { this.res = res; }

    public String getStamp() { return stamp; }
    public void setStamp(String stamp) { this.stamp = stamp; }

    public String getTz() { return tz; }
    public void setTz(String tz) { this.tz = tz; }
}