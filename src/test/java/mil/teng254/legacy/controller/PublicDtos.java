package mil.teng254.legacy.controller;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

public class PublicDtos {
    @XmlRootElement
    @Data
    public static class ResponseCommon {
        private Long res;
        private String stamp;
        private String tz;
        private String report;
        private String headersInfo;
    }

    @XmlRootElement
    @Data
    public static class RequestCommon implements Serializable {
        private Long key;
        private String stamp;
        private String optionalField;
        private Object choiceField;
    }

    @XmlRootElement
    @Data
    public static class RequestTimeStamp implements Serializable {
        private String uuid;
        private int shift;
        private String dayTimestamp;
    }

    @XmlRootElement
    @Data
    public static class ResponseTimeStamp implements Serializable {
        private String uuid;
        private String dayTimestamp;
    }

}
