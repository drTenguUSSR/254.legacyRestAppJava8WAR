package mil.teng254.legacy.services;

import lombok.extern.slf4j.Slf4j;
import mil.teng254.legacy.dto.RequestDto;
import mil.teng254.legacy.dto.ResponseDto;
import mil.teng254.legacy.filter.WebFilterSaveHeader;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class HelloServices {
    public Response processRequest(RequestDto request) {

        ResponseDto response = new ResponseDto();

        // Инкрементируем key
        response.setRes(request.getKey() + 1);

        // Парсим время, добавляем 1 час и форматируем обратно
        ZonedDateTime stampTime = ZonedDateTime.parse(request.getStamp());
        ZonedDateTime newTime = stampTime.plusHours(1);
        response.setStamp(newTime.format(DateTimeFormatter.ISO_INSTANT));

        // Получаем временную зону сервера
        ZoneId zone = ZoneId.systemDefault();
        response.setTz(zone.getRules().getOffset(newTime.toInstant()).toString());


        fillHeadersInfo(response);

        return Response.ok(response).build();
    }

    /**
     * заполнение mil.teng254.legacy.dto.ResponseDto#headersInfo
     * @param response
     */
    private void fillHeadersInfo(ResponseDto response) {
        String alfa = WebFilterSaveHeader.getHeaderAlfa();
        String bravo = WebFilterSaveHeader.getBravoData();
        String kilo = ThreadContext.get(WebFilterSaveHeader.CUST_LOG4J_PROP_KILO);
        String result = "alfa=" + alfa + ";bravo=" + bravo + ";kilo=" + kilo + ";";
        log.debug("fillHeadersInfo:{}",result);
        response.setHeadersInfo(result);
    }
}
