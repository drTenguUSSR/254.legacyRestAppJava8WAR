package mil.teng254.legacy.controller;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.grizzly.web.GrizzlyWebTestContainerFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.MediaType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//SPECIAL_PORT=1414
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/applicationContext.xml")
@Slf4j
public class PublicControllerIntegrationTest extends JerseyTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    protected TestContainerFactory getTestContainerFactory() {
        return new GrizzlyWebTestContainerFactory();
    }

    @Override
    protected WebAppDescriptor configure() {
        return new WebAppDescriptor.Builder()
                .contextPath("api")
                .contextParam("contextConfigLocation", "classpath:test-applicationContext.xml")
                .contextListenerClass(org.springframework.web.context.ContextLoaderListener.class)
                .servletClass(com.sun.jersey.spi.spring.container.servlet.SpringServlet.class)
                .initParam("com.sun.jersey.config.property.packages", "mil.teng254.legacy.controller")
                .initParam("com.sun.jersey.api.json.POJOMappingFeature", "false")
                .build();
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        // Дополнительная инициализация, если требуется
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void validGetTime() {
        // Создание запроса к endpoint
        WebResource webResource = resource().path("/public/time");

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
        PublicDtos.ResponseDto data = response.getEntity(PublicDtos.ResponseDto.class);
        log.debug("data={}", data);
        Assert.assertEquals("2023-11-30T20:45:59.192345678Z", data.getReport());
    }

    @Test
    public void validPostHelloRest_min() {
        WebResource webResource = resource().path("/public/hello-rest");

        String srcData = "{\"key\": 100, \"stamp\":\"2025-06-20T11:24:36Z\"}";
        ClientResponse response = webResource
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, srcData);

        // статус
        Assert.assertEquals(200, response.getStatus());
        PublicDtos.ResponseDto respData = response.getEntity(PublicDtos.ResponseDto.class);
        Assert.assertEquals(Long.valueOf(101), respData.getRes());
        Assert.assertEquals("2025-06-20T12:24:36Z", respData.getStamp());
        Assert.assertEquals("+03:00", respData.getTz());
    }

    @Test
    public void testContext() {
        // Проверяем, что бин "templateEngine" существует
        Assert.assertNotNull(applicationContext.getBean("templateEngine"));
        // Проверяем, что бин "currentTimeResource" существует и в него внедрен TemplateEngine
        CurrentTimeResource resource = applicationContext.getBean(CurrentTimeResource.class);
        Assert.assertNotNull(resource); // Убедитесь, что сам ресурс не null
    }

    @Test
    public void testCurrentTimeEndpointReturnsValidHtml() {
        // Создание запроса к эндпоинту времени
        WebResource webResource = resource().path("dtm-now");

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
                +"pat=[" + regExp + "] text=[\n"+htmlResponse+"\n]", matcher.find());
    }
}