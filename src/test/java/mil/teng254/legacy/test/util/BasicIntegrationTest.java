package mil.teng254.legacy.test.util;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mil.teng254.legacy.controller.StaticHolder;
import org.apache.catalina.LifecycleException;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.UUID;

@Slf4j
//MAIN_PORT=1415;SPECIAL_PORT=1414
public class BasicIntegrationTest {
    private static final String TEST_ID_ATT = "X-Cust-Att-Holder";

    @Autowired
    @Getter
    private WebApplicationContext applicationContext;

    @Getter
    private static EmbeddedTomcatServer tomcatServer;

    @Getter
    private static Client jerseyClient;

    public static String getTestId() {
        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert ra != null;
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
    public WebResource mainResource() {
        return jerseyClient.resource(tomcatServer.getMainBaseUrl() + "/api");
    }

    public WebResource specialResource() {
        return jerseyClient.resource(tomcatServer.getSpecialBaseUrl() + "/api");
    }
}
