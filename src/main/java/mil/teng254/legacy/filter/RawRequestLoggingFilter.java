package mil.teng254.legacy.filter;

import com.google.common.io.ByteStreams;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.ext.Provider;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Provider
@Slf4j
public class RawRequestLoggingFilter implements ContainerRequestFilter {

    public RawRequestLoggingFilter() {
        log.debug(".ctor");
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        if (request.getHeaderValue("Content-Type") != null &&
                request.getHeaderValue("Content-Type").contains("application/json")) {

            try {
                // Читаем и кэшируем тело запроса в байтовый массив
                byte[] bodyBytes = ByteStreams.toByteArray(request.getEntityInputStream());
                String bodyString = new String(bodyBytes, "UTF-8");

                // Логируем сырой JSON
                log.debug("\n==========Raw JSON Request Body: path={} port={} [\n{}\n==========]",
                        request.getPath(), request.getBaseUri().getPort(), bodyString);

                // Подменяем InputStream запроса на новый, прочитанный из кэша
                request.setEntityInputStream(new ByteArrayInputStream(bodyBytes));

            } catch (IOException e) {
                // Логируем ошибку чтения, но не прерываем выполнение
                log.error("Error reading request body for logging", e);
                // Возвращаем запрос как есть, обработка продолжится
            }
        }
        return request;
    }
}
