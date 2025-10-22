package mil.teng254.legacy.controller;

import lombok.extern.slf4j.Slf4j;
import mil.teng254.legacy.dto.CommonRequestDto;
import mil.teng254.legacy.dto.CommonResponseDto;
import mil.teng254.legacy.filter.SpecialBravoPort;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Path("/special-bravo")
@Component
@SpecialBravoPort
@Slf4j
public class SpecialBravoController {

    @POST
    @Path("/mark")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response markPath(JAXBElement<CommonRequestDto> wrequest) {
        CommonResponseDto response = new CommonResponseDto();

        StringBuilder report = new StringBuilder();

        CommonRequestDto request = wrequest.getValue();
        if (request.getOptionalField() != null) {
            report.append("OptionalField=[").append(request.getOptionalField()).append("]");
        } else {
            report.append("OptionalField=!null");
        }
        if (request.getChoiceField() != null) {
            String xtype = request.getChoiceField().getClass().getSimpleName();
            Object xval = request.getChoiceField();
            report.append(";ChoiceField=[").append(xtype).append(":").append(xval).append("]");
        } else {
            report.append(";ChoiceField=!null");
        }

        response.setReport(report.toString());
        // Инкрементируем key
        response.setRes(request.getKey() + 1);

        // Парсим время, добавляем 1 час и форматируем обратно
        ZonedDateTime stampTime = ZonedDateTime.parse(request.getStamp());
        ZonedDateTime newTime = stampTime.plusHours(1);
        response.setStamp(newTime.format(DateTimeFormatter.ISO_INSTANT));

        // Получаем временную зону сервера
        ZoneId zone = ZoneId.systemDefault();
        response.setTz(zone.getRules().getOffset(newTime.toInstant()).toString());
        log.debug("result:{}",response);

        return Response.ok(response).build();
    }
}