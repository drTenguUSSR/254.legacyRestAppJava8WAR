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
public class SystemIT extends BasicIntegrationTest {
    @Test
    public void validGetTechJaxbXml() {
        // Создание запроса к endpoint
        WebResource webResource = mainResource().path("/public/tech-jaxb-xml");

        // Выполнение GET запроса
        ClientResponse response = webResource
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        // статус
        Assert.assertEquals(200, response.getStatus());
        // проверка Content-Type
        String contentType = response.getHeaders().getFirst("Content-Type");
        Assert.assertTrue("Content-Type should be application/json",
                contentType != null && contentType.contains(MediaType.APPLICATION_JSON));

        //проверка Dto
        PublicDtos.ResponseCommon data = response.getEntity(PublicDtos.ResponseCommon.class);
        log.debug("data:{}", data.getReport());
        Assert.assertEquals("JAXBContext.class=[com.sun.xml.bind.v2.runtime.JAXBContextImpl]", data.getReport());
    }

    @Test
    public void testBothPortsAccessible() {
        // Тестируем доступ через основной порт
        WebResource mainResource = mainResource().path("/public/time");
        ClientResponse mainResponse = mainResource.get(ClientResponse.class);
        Assert.assertEquals(200, mainResponse.getStatus());

        // Тестируем доступ через специальный порт
        WebResource specialResource = specialResource().path("/public/time");
        ClientResponse specialResponse = specialResource.get(ClientResponse.class);
        Assert.assertEquals(200, specialResponse.getStatus());

        log.info("Both ports are accessible - Main: {}, Special: {}",
                getTomcatServer().getMainPort(), getTomcatServer().getSpecialPort());
    }

    @Test
    public void testContext() {
        // Проверяем, что бин "templateEngine" существует
        Assert.assertNotNull(getApplicationContext().getBean("templateEngine"));
        // Проверяем, что бин "currentTimeResource" существует и в него внедрен TemplateEngine
        CurrentTimeResource resource = getApplicationContext().getBean(CurrentTimeResource.class);
        Assert.assertNotNull(resource);
    }
}
