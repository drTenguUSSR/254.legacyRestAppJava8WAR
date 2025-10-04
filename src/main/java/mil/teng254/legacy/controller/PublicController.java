package mil.teng254.legacy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.teng254.legacy.dto.CommonRequestDto;
import mil.teng254.legacy.dto.RequestTimeStampDto;
import mil.teng254.legacy.dto.CommonResponseDto;
import mil.teng254.legacy.dto.ResponseTimeStampDto;
import mil.teng254.legacy.services.HelloServices;
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
@RequiredArgsConstructor
public class PublicController {
    private final HelloServices helloServices;

    @GET
    @Path("/time")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocalDtm() {
        ZonedDateTime dtm = ZonedDateTime.now();
        String dtmStamp = dtm.format(DateTimeFormatter.ISO_INSTANT);
        CommonResponseDto resp = new CommonResponseDto();
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
    public Response helloPath(CommonRequestDto request) {
        CommonResponseDto resp = helloServices.processRequest(request);
        return Response.ok(resp).build();
    }

    @POST
    @Path("/hello-rest")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response helloRest(CommonRequestDto request, @Context HttpServletRequest httpRequest) {
        log.info("helloRest. processRequest: thId={} dto.key={} localPort={} req.URI={}",
                Thread.currentThread().getId(), request.getKey(), httpRequest.getLocalPort(),
                httpRequest.getRequestURI());
        CommonResponseDto resp = helloServices.processRequest(request);
        return Response.ok(resp).build();
    }

    @POST
    @Path("/hello-rus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response helloRus(CommonRequestDto request,
                             @Context HttpServletRequest httpRequest,
                             @HeaderParam(StaticHolder.HTTP_HEADER_TEST_ID) String testId
    ) {
        log.info("helloRus. processRequest: dto.key={} remoteAdr={} localPort={} req.Method={} req.URI={}",
                request.getKey(),
                httpRequest.getRemoteAddr(), httpRequest.getLocalPort(),
                httpRequest.getMethod(), httpRequest.getRequestURI());
        CommonResponseDto resp = helloServices.helloRus(request);
        return Response.ok(resp).build();
    }

    @POST
    @Path("/days-shifter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response helloDayShifter(RequestTimeStampDto request,
                                    @Context HttpServletRequest httpRequest,
                                    @HeaderParam(StaticHolder.HTTP_HEADER_TEST_ID) String testId
    ) {
        log.info("helloDaysShifter. processRequest: dto.uuid={} localPort={} req.URI={}",
                request.getUuid(), httpRequest.getLocalPort(), httpRequest.getRequestURI());
        ResponseTimeStampDto resp = helloServices.helloDayShifter(request);
        return Response.ok(resp).build();
    }

}
