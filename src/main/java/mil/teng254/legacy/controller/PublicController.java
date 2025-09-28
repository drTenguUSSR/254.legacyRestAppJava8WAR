package mil.teng254.legacy.controller;

import lombok.extern.slf4j.Slf4j;
import mil.teng254.legacy.dto.RequestDto;
import mil.teng254.legacy.dto.ResponseDto;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Path("/public")
@Component
@Slf4j
public class PublicController {

    @Context
    private HttpServletRequest httpRequest;

    @GET
    @Path("/time")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocalDtm() {
        ZonedDateTime dtm = ZonedDateTime.now();
        String dtmStamp = dtm.format(DateTimeFormatter.ISO_INSTANT);
        ResponseDto resp = new ResponseDto();
        resp.setStamp(dtmStamp);

        ZoneId zoneId = ZoneId.of("UTC+3");
        ZonedDateTime zdt = ZonedDateTime.of(2023, 11, 30, 23, 45, 59, 192345678, zoneId);
        String zdtStamp = zdt.format(DateTimeFormatter.ISO_INSTANT);
        resp.setReport(zdtStamp);
        return Response.ok(resp).build();
    }

    @POST
    @Path("/hello-path")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response helloPath(RequestDto request) {
        return processRequest(request);
    }

    @POST
    @Path("/hello-rest")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response helloRest(RequestDto request) {
        return processRequest(request);
    }

    private Response processRequest(RequestDto request) {
        log.info("processRequest: remoteAdr={} localPort={}", httpRequest.getRemoteAddr(),httpRequest.getLocalPort());
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
