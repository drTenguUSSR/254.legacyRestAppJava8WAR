package mil.teng254.legacy.controller;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.grizzly2.web.GrizzlyWebTestContainerFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

//SPECIAL_PORT environment variable is required
//в конфигурации запуска test: переменная среда SPECIAL_PORT=4141

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/test/resources/test-applicationContext.xml")
@WebAppConfiguration
public class PublicControllerTest extends JerseyTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        // Создаем MockServletContext и регистрируем ContextLoaderListener
        MockServletContext servletContext = new MockServletContext();
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);

        // Создаем MockMvc с настроенным контекстом
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    public PublicControllerTest() {
        super(new WebAppDescriptor.Builder("mil.teng254.legacy.controller") // Пакет с контроллерами
                //.contextParam("contextConfigLocation", "/WEB-INF/applicationContext.xml")
                .contextParam("contextConfigLocation", "file:src/test/resources/test-applicationContext.xml")
                .servletClass(com.sun.jersey.spi.spring.container.servlet.SpringServlet.class)
                .build());
    }


//    @BeforeClass
//    public static void setupClass() {
//        System.setProperty("MY_PROPERTY", "my_value");
//    }

    @Test
    public void helloRest() {
        WebResource webResource = resource();
        Response response = webResource.path("/api/public/hello-rest") // URL вашего эндпоинта
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());
        // Дополнительные проверки тела ответа
    }
}