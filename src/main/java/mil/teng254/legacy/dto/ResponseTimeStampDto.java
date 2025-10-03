package mil.teng254.legacy.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import mil.teng254.legacy.filter.LocalDateAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@ToString
public class ResponseTimeStampDto {
    private String uuid;

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate dayTimestamp;
}
