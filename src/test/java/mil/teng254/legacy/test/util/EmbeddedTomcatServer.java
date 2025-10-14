//src/test/java/mil/teng254/legacy/test/util/EmbeddedTomcatServer.java
package mil.teng254.legacy.test.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
public class EmbeddedTomcatServer {
    private Tomcat tomcat;
    private final int mainPort;
    private final int specialPort;
    private final String tempBaseDir;
    private boolean started = false;

    public EmbeddedTomcatServer() {
        String mainPortStr = System.getenv("MAIN_PORT");
        String specialPortStr = System.getenv("SPECIAL_PORT");

        if (mainPortStr == null || specialPortStr == null) {
            throw new IllegalStateException("MAIN_PORT and SPECIAL_PORT environment variables must be set");
        }

        this.mainPort = Integer.parseInt(mainPortStr);
        this.specialPort = Integer.parseInt(specialPortStr);
        this.tempBaseDir = createTempBaseDir();

        log.info("EmbeddedTomcatServer initialized. Main port: {}, Special port: {}, Base dir: {}",
                mainPort, specialPort, tempBaseDir);
    }

    public void start() throws LifecycleException, ServletException, IOException {
        if (started) {
            log.warn("Server already started");
            return;
        }

        setupTomcatDirectories();
        createAndDeployWebapp();

        tomcat = new Tomcat();
        tomcat.setBaseDir(tempBaseDir);
        tomcat.setPort(mainPort); // Основной порт

        // Настраиваем основной коннектор
        Connector mainConnector = tomcat.getConnector();
        mainConnector.setPort(mainPort);
        mainConnector.setProperty("address", "0.0.0.0");

        // Добавляем второй коннектор для специального порта
        Connector specialConnector = new Connector("HTTP/1.1");
        specialConnector.setPort(specialPort);
        specialConnector.setProperty("address", "0.0.0.0");
        specialConnector.setProperty("connectionTimeout", "20000");

        // Добавляем оба коннектора к сервису
        tomcat.getService().addConnector(specialConnector);

        // Запускаем Tomcat
        tomcat.start();
        started = true;

        log.info("EmbeddedTomcatServer started successfully on ports {} (main) and {} (special)",
                mainPort, specialPort);
    }

    public void stop() throws LifecycleException {
        if (tomcat != null && started) {
            tomcat.stop();
            tomcat.destroy();
            started = false;
            log.info("EmbeddedTomcatServer stopped");
        }

        cleanupTempFiles();
    }

    public int getMainPort() {
        return mainPort;
    }

    public int getSpecialPort() {
        return specialPort;
    }

    public String getMainBaseUrl() {
        return "http://localhost:" + mainPort;
    }

    public String getSpecialBaseUrl() {
        return "http://localhost:" + specialPort;
    }

    public boolean isStarted() {
        return started;
    }

    private String createTempBaseDir() {
        try {
            Path tempDir = Files.createTempDirectory("embedded-tomcat-");
            return tempDir.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp directory", e);
        }
    }

    private void setupTomcatDirectories() throws IOException {
        String[] dirs = {
                "webapps",
                "logs",
                "work",
                "temp"
        };

        for (String dir : dirs) {
            Files.createDirectories(Paths.get(tempBaseDir, dir));
        }
        log.debug("Tomcat directory structure created in: {}", tempBaseDir);
    }

    private void createAndDeployWebapp() throws IOException {
        // Создаем структуру веб-приложения
        Path webappDir = Paths.get(tempBaseDir, "webapps/ROOT");
        Files.createDirectories(webappDir);

        // Создаем WEB-INF и поддиректории
        Path webInfDir = webappDir.resolve("WEB-INF");
        Files.createDirectories(webInfDir);
        Files.createDirectories(webInfDir.resolve("classes"));
        Files.createDirectories(webInfDir.resolve("lib"));

        // Создаем web.xml из ресурсного файла
        createWebXml(webInfDir);

        // Используем addWebapp для развертывания приложения:cite[1]
        // Это заменит использование setConfigFile
        if (tomcat != null) {
            tomcat.addWebapp("", webappDir.toAbsolutePath().toString());
        }

        log.info("Web application deployed to: {}", webappDir);
    }

    private void createWebXml(Path webInfDir) throws IOException {
        // Загружаем содержимое web.xml из ресурсного файла
        String webXmlContent = loadWebXmlFromResources();

        // Записываем содержимое в файл
        Path webXmlPath = webInfDir.resolve("web.xml");
        Files.write(webXmlPath, webXmlContent.getBytes(StandardCharsets.UTF_8));

        log.debug("web.xml created from resources at: {}", webXmlPath);
    }

    private String loadWebXmlFromResources() throws IOException {
        // Загружаем web.xml из ресурсов:cite[8]
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("tomcat-config/web.xml")) {
            if (is == null) {
                throw new IOException("web.xml not found in resources/tomcat-config/");
            }
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        }
    }

    private void cleanupTempFiles() {
        try {
            FileUtils.deleteDirectory(new File(tempBaseDir));
            log.debug("Temp directory cleaned: {}", tempBaseDir);
        } catch (IOException e) {
            log.warn("Failed to clean temp directory: {}", tempBaseDir, e);
        }
    }
}