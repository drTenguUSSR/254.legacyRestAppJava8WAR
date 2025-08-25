package mil.teng254.legacy.controller;

import mil.teng254.legacy.dto.RequestDto;
import mil.teng254.legacy.dto.ResponseDto;
import mil.teng254.legacy.filter.SpecialPort;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Path("/special")
@Component
public class SpecialController {

    @POST
    @Path("/mark-path")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @SpecialPort
    public Response markPath(RequestDto request) {
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

        return Response.ok(response).build();
    }
}