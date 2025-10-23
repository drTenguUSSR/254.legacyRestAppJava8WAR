//src/test/java/mil/teng254/legacy/controller/PublicControllerIntegrationTest.java
package mil.teng254.legacy.controller;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import lombok.extern.slf4j.Slf4j;
import mil.teng254.legacy.test.util.BasicIntegrationTest;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.ws.rs.core.MediaType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext.xml")
@WebAppConfiguration
@Slf4j
public class PublicIT extends BasicIntegrationTest {

    @Test
    public void validGetTime() {
        // Создание запроса к endpoint
        WebResource webResource = mainResource().path("/public/time");

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
        log.debug("data={}", data);
        // Замените на актуальные ожидаемые значения
        Assert.assertNotNull(data.getReport());
    }

    @Test
    public void validPostHelloRest_min() {
        log.debug("validPostHelloRest_min. testId={}", getTestId());
        WebResource webResource = mainResource().path("/public/hello-rest");

        String srcData = "{\"key\": 100, \"stamp\":\"2025-06-20T11:24:36Z\"}";
        ClientResponse response = webResource
                .header(StaticHolder.HTTP_HEADER_TEST_ID, getTestId())
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, srcData);

        // статус
        Assert.assertEquals(200, response.getStatus());
        PublicDtos.ResponseCommon respData = response.getEntity(PublicDtos.ResponseCommon.class);
        Assert.assertEquals(Long.valueOf(101), respData.getRes());
        // Добавьте проверки для других полей
    }

    @Test
    public void validPostHelloRestMultiHeader() {
        log.debug("validPostHelloRestMultiHeader. testId={}", getTestId());
        WebResource webResource = mainResource().path("/public/hello-rest");

        String srcData = "{\"key\": 150, \"stamp\":\"2025-06-20T11:24:36Z\"}";
        ClientResponse response = webResource
                .header(StaticHolder.HTTP_HEADER_TEST_ID, getTestId())
                .header("X-Cust-Alfa", "AA01")
                .header("X-Cust-Bravo", "BB02")
                .header("X-Cust-Kilo", "KK03")
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, srcData);
        // статус
        Assert.assertEquals(200, response.getStatus());
        PublicDtos.ResponseCommon respData = response.getEntity(PublicDtos.ResponseCommon.class);
        Assert.assertEquals(Long.valueOf(151), respData.getRes());
    }

    @Test
    public void testSpecialPortAccess() {
        log.debug("testSpecialPortAccess. testId={}", getTestId());
        WebResource webResource = specialResource().path("/public/time");

        ClientResponse response = webResource
                .header(StaticHolder.HTTP_HEADER_TEST_ID, getTestId())
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        Assert.assertEquals(200, response.getStatus());
        log.info("Successfully accessed endpoint via special port: {}", getTomcatServer().getSpecialPort());
    }

    @Test
    public void testCurrentTimeEndpointReturnsValidHtml() {
        // Создание запроса к эндпоинту времени
        WebResource webResource = mainResource().path("dtm-now");

        // Выполнение GET запроса
        String htmlResponse = webResource
                .accept(MediaType.TEXT_HTML)
                .get(String.class);

        // Проверка, что ответ является HTML и содержит ожидаемые элементы
        Assert.assertNotNull("HTML response should not be null", htmlResponse);
        Assert.assertTrue("Response should contain HTML title", htmlResponse.contains("<title>Current Time</title>"));
        Assert.assertTrue("Response should contain the header", htmlResponse.contains("Текущая дата и время (UTC)"));

        // Проверка формата даты (примерная проверка на соответствие шаблону ГГГГ-ММ-ДД)
        // Это проверяет, что Thymeleaf обработал выражение с датой
        String regExp = ".*via=\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3,9}Z.*";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(htmlResponse);
        Assert.assertTrue("Response should contain a date string."
                + "pat=[" + regExp + "] text=[\n" + htmlResponse + "\n]", matcher.find());
    }
}