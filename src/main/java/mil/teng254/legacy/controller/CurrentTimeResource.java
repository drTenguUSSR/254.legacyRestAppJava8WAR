package mil.teng254.legacy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringWriter;
import java.util.Locale;

// Путь к эндпоинту, например,
// http://localhost:8081/api/dtm-now
@Path("/dtm-now")
@Service
public class CurrentTimeResource {

    @Autowired
    private TemplateEngine templateEngine;
    private final Locale ruLocale=new Locale("ru","RU");

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getCurrentTime() {
        try {
            //TemplateEngine templateEngine = SpringContextHolder.getBean("templateEngine");
            // Создаем простой контекст Thymeleaf
            Context thymeleafContext = new Context();

            thymeleafContext.setLocale(ruLocale);

            // Добавляем переменные в контекст для шаблона
            // Например, текущую дату-время в UTC
            thymeleafContext.setVariable("currentUtcTime",
                    java.time.Instant.now().toString());

            // Обработка шаблона
            StringWriter writer = new StringWriter();
            templateEngine.process("current-time", thymeleafContext, writer);
            String htmlContent = writer.toString();

            return Response.ok(htmlContent).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error processing template: " + e.getMessage()).build();
        }
    }

}