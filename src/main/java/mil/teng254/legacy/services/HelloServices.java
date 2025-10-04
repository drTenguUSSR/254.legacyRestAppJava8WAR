package mil.teng254.legacy.services;

import lombok.extern.slf4j.Slf4j;
import mil.teng254.legacy.dto.CommonRequestDto;
import mil.teng254.legacy.dto.RequestTimeStampDto;
import mil.teng254.legacy.dto.CommonResponseDto;
import mil.teng254.legacy.dto.ResponseTimeStampDto;
import mil.teng254.legacy.filter.SaveXCustHeadersServletFilter;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class HelloServices {
    public CommonResponseDto processRequest(CommonRequestDto request) {
        CommonResponseDto response = new CommonResponseDto();
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
        return response;
    }

    /**
     * заполнение mil.teng254.legacy.dto.ResponseDto#headersInfo
     *
     * @param response заполненный ResponseDto с данными из заголовков
     */
    private void fillHeadersInfo(CommonResponseDto response) {
        String alfa = SaveXCustHeadersServletFilter.getHeaderAlfa();
        String bravo = SaveXCustHeadersServletFilter.getBravoData();
        String kilo = ThreadContext.get(SaveXCustHeadersServletFilter.CUST_LOG4J_PROP_KILO);
        String result = "alfa=" + alfa + ";bravo=" + bravo + ";kilo=" + kilo + ";";
        log.debug("fillHeadersInfo:{}", result);
        response.setHeadersInfo(result);
    }

    public CommonResponseDto helloRus(CommonRequestDto request) {
        CommonResponseDto response = new CommonResponseDto();
        log.debug("helloRus: req=!{}!", request.getStamp());
        String result = "Результат проверки от " + request.getStamp() + " отправлен";
        log.debug("helloRus: result=!{}!", result);
        response.setRes(request.getKey() + 1);
        response.setStamp(result);
        return response;
    }

    public ResponseTimeStampDto helloDayShifter(RequestTimeStampDto request) {
        final String PREFIX = "helloDayShifter:";
        ResponseTimeStampDto resp = new ResponseTimeStampDto();
        log.debug(PREFIX + "req={}", request);
        resp.setUuid(request.getUuid());
        LocalDate dtm = request.getDayTimestamp();
        LocalDate respDtm = dtm.plusDays(request.getShift());
        resp.setDayTimestamp(respDtm);
        log.debug("helloDayShifter: resp={}", resp);
        return resp;
    }
}
