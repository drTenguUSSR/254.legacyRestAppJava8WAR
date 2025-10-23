package mil.teng254.legacy.controller;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import lombok.extern.slf4j.Slf4j;
import mil.teng254.legacy.test.util.BasicIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.ws.rs.core.MediaType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext.xml")
@WebAppConfiguration
@Slf4j
public class SpecialMarkIT  extends BasicIntegrationTest {

    //@Test
    public void FunA() {
        WebResource webResource = specialResource().path("/special-bravo/mark");

        String srcData = "{\"key\": 100, \"stamp\":\"2025-06-20T11:24:36Z\"}";
        ClientResponse response = webResource
                .header(StaticHolder.HTTP_HEADER_TEST_ID, getTestId())
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, srcData);

        Assert.assertEquals(200, response.getStatus());
        PublicDtos.ResponseCommon respData = response.getEntity(PublicDtos.ResponseCommon.class);
        Assert.assertEquals(Long.valueOf(101), respData.getRes());
    }

    @Test
    public void FunB() {
        log.debug("FunB-BEG");
        WebResource webResource = mainResource().path("/special-bravo/mark");

        String srcData = "{\"key\": 100, \"stamp\":\"2025-06-20T11:24:36Z\"}";
        ClientResponse response = webResource
                .header(StaticHolder.HTTP_HEADER_TEST_ID, getTestId())
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, srcData);

        Assert.assertEquals(200, response.getStatus());
        PublicDtos.ResponseCommon respData = response.getEntity(PublicDtos.ResponseCommon.class);
        Assert.assertEquals(Long.valueOf(101), respData.getRes());
        log.debug("FunB-END");
    }
}
