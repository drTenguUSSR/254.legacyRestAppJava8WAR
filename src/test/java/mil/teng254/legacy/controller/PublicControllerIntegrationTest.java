//src/test/java/mil/teng254/legacy/controller/PublicControllerIntegrationTest.java
package mil.teng254.legacy.controller;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import lombok.extern.slf4j.Slf4j;
import mil.teng254.legacy.test.util.EmbeddedTomcatServer;
import mil.teng254.legacy.filter.test.OverrideRequestAttributesFilter;
import mil.teng254.legacy.services.ServiceRequestUpdater;
import org.apache.catalina.LifecycleException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext.xml")
@WebAppConfiguration
@Slf4j
public class PublicControllerIntegrationTest {

    private static final String TEST_ID_ATT = "X-Cust-Att-Holder";

    @Autowired
    private WebApplicationContext applicationContext;

    private static EmbeddedTomcatServer tomcatServer;
    private static Client jerseyClient;

    private static String getTestId() {
        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String testId = (String) ra.getRequest().getAttribute(TEST_ID_ATT);
        Assert.assertNotNull(testId);
        return testId;
    }

    @BeforeClass
    public static void setUpClass() throws LifecycleException, ServletException, IOException {
        log.info("Starting EmbeddedTomcatServer for test class...");

        tomcatServer = new EmbeddedTomcatServer();
        tomcatServer.start();

        // Инициализируем Jersey клиент
        jerseyClient = Client.create();

        log.info("EmbeddedTomcatServer started successfully");
    }

    @AfterClass
    public static void tearDownClass() throws LifecycleException {
        log.info("Stopping EmbeddedTomcatServer...");

        if (jerseyClient != null) {
            jerseyClient.destroy();
        }

        if (tomcatServer != null) {
            tomcatServer.stop();
        }

        log.info("EmbeddedTomcatServer stopped");
    }

    @Before
    public void setUp() {
        // Создаем мок-объекты HTTP-запроса и ответа
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Устанавливаем необходимые атрибуты запроса
        String testId = UUID.randomUUID().toString();
        log.debug("setUp. testId={}", testId);
        request.setAttribute(TEST_ID_ATT, testId);

        // Привязываем атрибуты запроса к текущему потоку
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request, response);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        StaticHolder.set(testId, requestAttributes);

        log.debug("RequestContext установлен для текущего потока");
    }

    @After
    public void tearDown() {
        // Очищаем контекст запроса после теста
        String testId = getTestId();
        log.debug("tearDown: for testId={}", testId);
        StaticHolder.remove(testId);
        RequestContextHolder.resetRequestAttributes();
    }

    // Вспомогательные методы для создания WebResource
    private WebResource resource() {
        return jerseyClient.resource(tomcatServer.getMainBaseUrl() + "/api");
    }

    private WebResource specialResource() {
        return jerseyClient.resource(tomcatServer.getSpecialBaseUrl() + "/api");
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
        PublicDtos.ResponseCommon data = response.getEntity(PublicDtos.ResponseCommon.class);
        log.debug("data={}", data);
        // Замените на актуальные ожидаемые значения
        Assert.assertNotNull(data.getReport());
    }

    @Test
    public void validGetTechJaxbXml() {
        // Создание запроса к endpoint
        WebResource webResource = resource().path("/public/tech-jaxb-xml");

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
        log.debug("data={}", data.getReport());
        Assert.assertNotNull(data.getReport());
    }

    @Test
    public void validPostHelloRest_min() {
        log.debug("validPostHelloRest_min. testId={}", getTestId());
        WebResource webResource = resource().path("/public/hello-rest");

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
        WebResource webResource = resource().path("/public/hello-rest");

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
        log.info("Successfully accessed endpoint via special port: {}", tomcatServer.getSpecialPort());
    }

    @Test
    public void testBothPortsAccessible() {
        // Тестируем доступ через основной порт
        WebResource mainResource = resource().path("/public/time");
        ClientResponse mainResponse = mainResource.get(ClientResponse.class);
        Assert.assertEquals(200, mainResponse.getStatus());

        // Тестируем доступ через специальный порт
        WebResource specialResource = specialResource().path("/public/time");
        ClientResponse specialResponse = specialResource.get(ClientResponse.class);
        Assert.assertEquals(200, specialResponse.getStatus());

        log.info("Both ports are accessible - Main: {}, Special: {}",
                tomcatServer.getMainPort(), tomcatServer.getSpecialPort());
    }

    // ... остальные существующие тестовые методы

    @Test
    public void testContext() {
        // Проверяем, что бин "templateEngine" существует
        Assert.assertNotNull(applicationContext.getBean("templateEngine"));
        // Проверяем, что бин "currentTimeResource" существует и в него внедрен TemplateEngine
        CurrentTimeResource resource = applicationContext.getBean(CurrentTimeResource.class);
        Assert.assertNotNull(resource);
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
                + "pat=[" + regExp + "] text=[\n" + htmlResponse + "\n]", matcher.find());
    }
}