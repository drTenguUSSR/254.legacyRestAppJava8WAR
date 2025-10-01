package mil.teng254.legacy.controller;

import lombok.extern.slf4j.Slf4j;
import mil.teng254.legacy.dto.RequestDto;
import mil.teng254.legacy.dto.ResponseDto;
import mil.teng254.legacy.services.HelloServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

@Path("/public")
@Component
@Slf4j
public class PublicController {

    @Context
    private HttpServletRequest httpRequest;

    @Autowired
    private HelloServices helloServices;

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
        ResponseDto resp = helloServices.processRequest(request);
        return Response.ok(resp).build();
    }

    @POST
    @Path("/hello-rest")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response helloRest(RequestDto request,
                              @Context HttpServletRequest httpRequest,
                              @HeaderParam(StaticHolder.HTTP_HEADER_TEST_ID) String testId
    ) {
        log.info("helloRest. processRequest: dto.key={} remoteAdr={} localPort={} req.Method={} req.URI={}",
                request.getKey(),
                httpRequest.getRemoteAddr(), httpRequest.getLocalPort(),
                httpRequest.getMethod(),httpRequest.getRequestURI());
        StaticHolder.dumpRequestInfo("param",httpRequest);
        StaticHolder.overrideRequestAttributes(testId,httpRequest);

        HttpServletRequest req2 = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();
        StaticHolder.dumpRequestInfo("inside",req2);

        //TODO: по окончании обработки запроса, нужно очистить контекст. в идеале - восстановить предыдущий (до override)
        ResponseDto resp = helloServices.processRequest(request);
        return Response.ok(resp).build();
    }
}
