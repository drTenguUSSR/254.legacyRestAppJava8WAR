package mil.teng254.legacy.controller;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.grizzly.web.GrizzlyWebTestContainerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/applicationContext.xml")
public class PointControllerIntegrationTest extends JerseyTest {

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
                .initParam("com.sun.jersey.api.json.POJOMappingFeature", "true")
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
    public void testPointEndpoint() {
        // Создание запроса к endpoint
        WebResource webResource = resource()
                .path("point");

        // Выполнение GET запроса
        Response response = webResource
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .get(Response.class);

        // Проверка статуса ответа
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Проверка содержимого ответа как строки
        String responseJson = (String) response.getEntity();

        // Проверка наличия ожидаемых полей в JSON
        assertTrue("Response should contain key1", responseJson.contains("\"key1\""));
        assertTrue("Response should contain key2", responseJson.contains("\"key2\""));
        assertTrue("Response should contain value 'aaa' for key1", responseJson.contains("\"aaa\""));
        assertTrue("Response should contain value 42 for key2", responseJson.contains("42"));
    }

    @Test
    public void testPointEndpointWithDTO() {
        // Если у вас есть DTO класс для ответа
        WebResource webResource = resource().path("point");

        // Выполнение запроса и десериализация в строку для проверки
        String response = webResource
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .get(String.class);

        // Проверка структуры JSON
        assertTrue(response.contains("\"key1\":\"aaa\""));
        assertTrue(response.contains("\"key2\":42"));
    }

    // Дополнительный тест для проверки Content-Type
//    @Test
//    public void testPointEndpointContentType() {
//        WebResource webResource = resource().path("point");
//
//        Response response = webResource
//                .accept(MediaType.APPLICATION_JSON)
//                .get(Response.class);
//
//        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
//        String contentType = response.getHeaders().getFirst("Content-Type");
//        assertTrue("Content-Type should be application/json",
//                contentType != null && contentType.contains(MediaType.APPLICATION_JSON));
//    }
}