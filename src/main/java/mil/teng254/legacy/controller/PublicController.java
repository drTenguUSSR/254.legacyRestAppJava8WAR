package mil.teng254.legacy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.teng254.legacy.dto.CommonRequestDto;
import mil.teng254.legacy.dto.CommonResponseDto;
import mil.teng254.legacy.dto.RequestTimeStampDto;
import mil.teng254.legacy.dto.ResponseTimeStampDto;
import mil.teng254.legacy.services.HelloServices;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.lang.annotation.Annotation;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Path("/public")
@Component
@Slf4j
@RequiredArgsConstructor
public class PublicController {
    private final HelloServices helloServices;
    @Context
    Providers providers;

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

    //http://localhost:8081/api/public/tech-info

    /**
     * --- default:
     * JAXBContext provider: [null]
     * JAXBContext class: [com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl]
     * ur.class=com.sun.jersey.json.impl.provider.entity.JSONRootElementProvider.App
     * --- target
     * JAXBContext provider: [null]
     * JAXBContext class: [com.sun.xml.bind.v2.runtime.JAXBContextImpl]
     * ur.class=com.sun.jersey.json.impl.provider.entity.JSONRootElementProvider.App
     *
     * @return
     * @throws JAXBException
     */

    @GET
    @Path("/tech-info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTechInfo() throws JAXBException {
        StringBuilder report = new StringBuilder();

        //javax.xml.bind.JAXBContextFactory - Если значение null или оно отсутствует, используется провайдер по умолчанию.
        report.append("\nJAXBContext provider: [").append(System.getProperty("javax.xml.bind.JAXBContextFactory")).append("]");
        JAXBContext context = JAXBContext.newInstance(CommonRequestDto.class);
        report.append("\nJAXBContext class: [").append(context.getClass().getName()).append("]");

        /**
         * Ожидаемые имена классов в зависимости от провайдера:
         * Jackson: Классы из пакетов org.codehaus.jackson.jaxrs или com.fasterxml.jackson.jaxrs.json
         * Jettison: Классы из пакета com.sun.jersey.json.impl
         * MOXy: Классы из пакета org.eclipse.persistence.jpa.rs.util
         * ---
         * Jersey JSON Provider: com.sun.jersey.json.impl.provider.entity.JSONRootElementProvider
         */
        MessageBodyReader<CommonRequestDto> ur = providers.getMessageBodyReader(CommonRequestDto.class,
                CommonRequestDto.class, new Annotation[0], MediaType.APPLICATION_JSON_TYPE);
        report.append("\nMessageBodyReader ur.class=").append(ur.getClass().getCanonicalName());

        String res = report.toString();
        log.debug("getTechInfo:{}", res);
        return Response.ok(res).build();
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
