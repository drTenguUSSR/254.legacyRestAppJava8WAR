package mil.teng254.legacy.controller;

import lombok.extern.slf4j.Slf4j;
import mil.teng254.legacy.dto.CommonRequestDto;
import mil.teng254.legacy.dto.CommonResponseDto;
import mil.teng254.legacy.filter.SpecialDeltaPort;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

@Path("/special-delta")
@Component
@SpecialDeltaPort
@Slf4j
public class SpecialDeltaController {

    @POST
    @Path("/mark")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response markPath(JAXBElement<CommonRequestDto> wrequest) {
        CommonResponseDto response = new CommonResponseDto();
        CommonRequestDto request = wrequest.getValue();
        // Инкрементируем key+7
        response.setRes(request.getKey() + 7);
        response.setReport("SpecialDeltaController");
        log.debug("result:{}",response);

        return Response.ok(response).build();
    }
}
