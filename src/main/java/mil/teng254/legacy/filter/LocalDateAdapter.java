package mil.teng254.legacy.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate unmarshal(String dateString) throws Exception {
        log.warn("unmarshal: !{}!", dateString);
        if (!StringUtils.hasText(dateString)) {
            return null;
        }
        return LocalDate.parse(dateString, LOCAL_DATE_FORMATTER);
    }

    @Override
    public String marshal(LocalDate localDate) throws Exception {
        if (localDate == null) {
            return null;
        }
        return LOCAL_DATE_FORMATTER.format(localDate);
    }
}