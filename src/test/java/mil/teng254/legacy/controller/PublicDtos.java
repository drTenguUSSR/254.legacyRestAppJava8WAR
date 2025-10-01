package mil.teng254.legacy.controller;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

public class PublicDtos {
    @XmlRootElement
    @Data
    public static class ResponseDto {
        private Long res;
        private String stamp;
        private String tz;
        private String report;
        private String headersInfo;
    }
}
