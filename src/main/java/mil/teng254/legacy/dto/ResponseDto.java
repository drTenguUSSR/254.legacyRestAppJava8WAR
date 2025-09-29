package mil.teng254.legacy.dto;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
@Data
public class ResponseDto implements Serializable {
    private Long res;
    private String stamp;
    private String tz;
    private String report;
    private String headersInfo;
}