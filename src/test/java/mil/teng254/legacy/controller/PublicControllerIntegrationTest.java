//src/test/java/mil/teng254/legacy/controller/PublicControllerIntegrationTest.java
package mil.teng254.legacy.controller;

import com.google.common.base.Strings;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.grizzly.web.GrizzlyWebTestContainerFactory;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import mil.teng254.legacy.filter.SaveXCustHeadersServletFilter;
import mil.teng254.legacy.filter.test.OverrideRequestAttributesFilter;
import mil.teng254.legacy.services.ServiceRequestUpdater;
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

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebFilter;
import javax.ws.rs.core.MediaType;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//SPECIAL_PORT=1414
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext.xml")
@WebAppConfiguration
@Slf4j
public class PublicControllerIntegrationTest extends JerseyTest {

    private static final String TEST_ID_ATT = "X-Cust-Att-Holder";

    @Autowired
    private WebApplicationContext applicationContext;

    private static String getTestId() {
        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String testId = (String) ra.getRequest().getAttribute(TEST_ID_ATT);
        Assert.assertNotNull(testId);
        return testId;
    }

    private static String getFilterName(@NonNull Class<? extends Filter> filterClass) {
        if (filterClass.isAnnotationPresent(WebFilter.class)) {
            WebFilter webFilterAnnotation = filterClass.getAnnotation(WebFilter.class);
            String filterName = webFilterAnnotation.filterName();
            if (Strings.isNullOrEmpty(filterName)) {
                throw new IllegalArgumentException("filter " + filterClass.getCanonicalName() + " must have a name");
            }
            return filterName;
        } else {
            throw new IllegalArgumentException("class " + filterClass.getCanonicalName() + " must have a @WebFilter");
        }
    }

    @AfterClass
    public static void cleanupAll() {
        StaticHolder.cleanupAll();
    }

    @Override
    protected TestContainerFactory getTestContainerFactory() {
        //https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest31x/test-framework.html
        //GrizzlyWebTestContainerFactory - поддерживает сервлеты
        //GrizzlyTestContainerFactory - облегченный HTTP-контейнер
        return new GrizzlyWebTestContainerFactory();
    }

    @Override
    protected WebAppDescriptor configure() {
        return new WebAppDescriptor.Builder()
                .contextPath("api")
                .contextParam("contextConfigLocation", "classpath:test-applicationContext.xml")
                .contextListenerClass(org.springframework.web.context.ContextLoaderListener.class)
                .servletClass(com.sun.jersey.spi.spring.container.servlet.SpringServlet.class)
                //.addFilter(DiagnosticFilter.class, "diagnosticFilter1")
                .addFilter(OverrideRequestAttributesFilter.class, "OverrideRequestAttributesFilter")
                //.addFilter(DiagnosticFilter.class, "diagnosticFilter2")
                .addFilter(SaveXCustHeadersServletFilter.class, getFilterName(SaveXCustHeadersServletFilter.class))
                .initParam("com.sun.jersey.config.property.packages",
                        "mil.teng254.legacy.controller,"
                                + "mil.teng254.legacy.filter")
                // Явная регистрация JAX-RS провайдеров в тестах. Запасной вариант, если сканирование не сработало
                //.initParam("com.sun.jersey.config.property.jaxrs.providers", "mil.teng254.legacy.filter.JAXBContentResolver")
                // Регистрация ContainerRequestFilters
                .initParam("com.sun.jersey.spi.container.ContainerRequestFilters",
                        "mil.teng254.legacy.filter.RawRequestLoggingFilter," +
                                "mil.teng254.legacy.filter.SpecialPortFilter")
                .initParam("com.sun.jersey.api.json.POJOMappingFeature", "false")
                .build();
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        // Создаем мок-объекты HTTP-запроса и ответа
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Устанавливаем необходимые атрибуты запроса
        //request.setMethod("POST");
        //request.setRequestURI("/api/publicERR/hello-rest");
        //request.setContentType(MediaType.APPLICATION_JSON);
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
    @Override
    public void tearDown() throws Exception {
        // Очищаем контекст запроса после теста
        String testId = getTestId();
        log.debug("tearDown: for testId={}", testId);
        StaticHolder.remove(testId);
        RequestContextHolder.resetRequestAttributes();
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
        PublicDtos.ResponseCommon data = response.getEntity(PublicDtos.ResponseCommon.class);
        log.debug("data={}", data);
        Assert.assertEquals("2023-11-30T20:45:59.192345678Z", data.getReport());
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
        Assert.assertEquals("JAXBContext class=[com.sun.xml.bind.v2.runtime.JAXBContextImpl]", data.getReport());
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
        Assert.assertEquals("2025-06-20T12:24:36Z", respData.getStamp());
        Assert.assertEquals("+03:00", respData.getTz());
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
        Assert.assertEquals("alfa=AA01;bravo=BB02;kilo=KK03;", respData.getHeadersInfo());
    }

    @Test
    public void validPostHelloRus() {
        log.debug("validPostHelloRus. testId={}", getTestId());
        WebResource webResource = resource().path("/public/hello-rus");

        String srcMsg = "внезапно-" + UUID.randomUUID();
        String resMsg = "Результат проверки от " + srcMsg + " отправлен";

        PublicDtos.RequestCommon srcData = new PublicDtos.RequestCommon();
        srcData.setKey(160L);
        srcData.setStamp(srcMsg);

        ClientResponse response = webResource
                .header(StaticHolder.HTTP_HEADER_TEST_ID, getTestId())
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, srcData);

        Assert.assertEquals(200, response.getStatus());

        PublicDtos.ResponseCommon respData = response.getEntity(PublicDtos.ResponseCommon.class);
        Assert.assertEquals(Long.valueOf(161), respData.getRes());
        Assert.assertEquals(resMsg, respData.getStamp());
    }

    @Test
    public void validPostDayShifter() {
        final String PREFIX = "validPostDayShifter:";
        log.debug(PREFIX + "testId={}", getTestId());
        WebResource webResource = resource().path("/public/days-shifter");

        PublicDtos.RequestTimeStamp srcData = new PublicDtos.RequestTimeStamp();
        String srcUuid = UUID.randomUUID().toString();
        srcData.setUuid(srcUuid);
        srcData.setShift(5);
        srcData.setDayTimestamp("2023-04-27");
        log.debug(PREFIX + "request={}", srcData);

        ClientResponse response = webResource
                .header(StaticHolder.HTTP_HEADER_TEST_ID, getTestId())
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, srcData);

        Assert.assertEquals(200, response.getStatus());

        PublicDtos.ResponseTimeStamp respData = response.getEntity(PublicDtos.ResponseTimeStamp.class);
        Assert.assertEquals(srcUuid, respData.getUuid());
        Assert.assertEquals("2023-05-02", respData.getDayTimestamp());
    }

    /**
     * Проверка spring-контекста
     */
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
                + "pat=[" + regExp + "] text=[\n" + htmlResponse + "\n]", matcher.find());
    }

    //===================================
    @Test
    public void testMockServletContextAndRequestContext() {
        log.info("=== Начало диагностики MockServletContext и RequestContext ===");

        // 1. Проверяем, что ApplicationContext загружен
        Assert.assertNotNull("ApplicationContext должен быть загружен", applicationContext);
        log.info("✅ ApplicationContext загружен: {}", applicationContext);

        // 2. Проверяем, что у нас есть WebApplicationContext
        if (applicationContext instanceof WebApplicationContext) {
            WebApplicationContext webAppContext = (WebApplicationContext) applicationContext;
            log.info("✅ ApplicationContext является WebApplicationContext");

            // 3. Проверяем ServletContext
            ServletContext servletContext = webAppContext.getServletContext();
            Assert.assertNotNull("ServletContext должен существовать", servletContext);
            log.info("✅ ServletContext: {}", servletContext);

            // 4. Проверяем, что это MockServletContext
            log.info("servletContext.class={}", servletContext.getClass().getCanonicalName());
            if (servletContext instanceof MockServletContext) {
                log.info("✅ ServletContext является MockServletContext");

                // 5. Проверяем атрибуты MockServletContext
                MockServletContext mockContext = (MockServletContext) servletContext;
                log.info("✅ ContextPath: {}", mockContext.getContextPath());
                //log.info("✅ VirtualServerName: {}", mockContext.getVirtualServerName());

            } else {
                log.warn("❌ ServletContext НЕ является MockServletContext, а: {}", servletContext.getClass());
            }
        } else {
            log.warn("❌ ApplicationContext НЕ является WebApplicationContext, а: {}", applicationContext.getClass());
        }

        // 6. Проверяем RequestContextHolder
        try {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            log.info("✅ RequestContextHolder имеет активные атрибуты: {}", requestAttributes);

            if (requestAttributes instanceof ServletRequestAttributes) {
                ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
                log.info("✅ Request: {}", servletRequestAttributes.getRequest());
                log.info("✅ Response: {}", servletRequestAttributes.getResponse());
            }
        } catch (IllegalStateException e) {
            log.error("❌ RequestContextHolder НЕ имеет активного запроса: {}", e.getMessage());
            log.info("⚠️  Это объясняет ошибку 'No thread-bound request found'");
        }

        // 7. Проверяем бины из контекста
        checkBeanExist(null, ServletContext.class);
        checkBeanExist("requestContextListener", RequestContextListener.class);
        checkBeanExist(null, ServiceRequestUpdater.class);
        log.info("=== Завершение диагностики ===");
    }

    /**
     * Проверка существования бина. Вначале выполняется поиск по классу. потом по имени (идентификатору)
     *
     * @param beanName идентификатор бина
     * @param clazz    класс бина
     */
    private void checkBeanExist(String beanName, Class<?> clazz) {
        Exception eClass;

        try {
            Object bean = applicationContext.getBean(clazz);
            log.info("✅ Бин {} из контекста-по-классу: {}", clazz.getCanonicalName(), bean);
            return;
        } catch (Exception e) {
            eClass = e;
        }

        if (beanName == null) {
            log.warn("❌ Не удалось получить бин-по-классу {}: {}", clazz.getCanonicalName(), eClass.getMessage());
            eClass.printStackTrace();
            return;
        }

        try {
            Object bean = applicationContext.getBean(beanName);
            log.info("✅ Бин {} из контекста-по-имени: {}", beanName, bean);
        } catch (Exception e) {
            log.warn("❌ Не удалось получить бин-по-классу {}: {}", clazz.getCanonicalName(), eClass.getMessage());
            eClass.printStackTrace();
            log.warn("❌ Не удалось получить бин-по-имени {}: {}", beanName, e.getMessage());
            e.printStackTrace();
        }

    }
}