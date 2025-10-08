package mil.teng254.legacy.controller;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.net.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class MicroLanguageTests {
    @Test
    public void testRegExpDate() {
        String src = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Current Time</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Текущая дата и время (UTC)</h1>\n" +
                "<!-- Используем переменную 'currentUtcTime' -->\n" +
                "<p>2025-09-27T22:06:28.856Z</p>\n" +
                "\n" +
                "<!-- Или для более сложного форматирования с помощью временных утилит Thymeleaf -->\n" +
                "<p>2025-09-28 01:06:29 +0300</p>\n" +
                "</body>\n" +
                "</html>";
        String regExp = ".*\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3,9}Z.*";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(src);
        Assert.assertTrue("pat=[" + regExp + "]", matcher.find());
    }

    @Test
    public void specCharTest() {
        String message = "spec-symbols:\0,␀,\u2401,\u2426,\u274C,\u274E,\u2049";
        message += ",\u27B0"; // Curly Loop, '➰'
        message += ",\u00BF"; //Inverted Question Mark, '¿'
        //(UTF-16 Encoding), see https://www.compart.com/en/unicode/block/U+1F300
        message += ",\uD83D\uDE2D"; //Loudly Crying Face Emoji Meaning,😭
        message += ",\uD83D\uDC0D"; // Snake,🐍
        message += ",\uD83D\uDD25"; // Fire,1F525,'🔥'
        message += ",\uD83D\uDDD1"; // Wastebasket,1F5D1,'🗑'
        message += ",\u2622\ufe0f"; // Эмодзи Радиация, '☢'
        message += ",\u2623\ufe0f"; // Эмодзи Биологическая Угроза, '☣️'
        log.debug("message:{}", message);
    }

    @Test
    public void getHostInfo() {
        HostInfo hostInfo = new HostInfo();
        String result = "host: " + hostInfo.getName() + ", addresses: " + hostInfo.getAddresses();
        log.debug("result: {}", result);
    }


    public static class HostInfo {
        private static final String PREFIX = "HostInfo:";


        public String getName() {
            String host;
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                host = System.getenv("COMPUTERNAME");
            } else {
                host = System.getenv("HOSTNAME");
            }
            if (Strings.isNullOrEmpty(host)) {
                try {
                    InetAddress xlocal = InetAddress.getLocalHost();
                    host = xlocal.getHostName();
                } catch (UnknownHostException e) {
                    log.error(PREFIX + "UnknownHostException", e);
                }
            }
            if (Strings.isNullOrEmpty(host)) {
                host = "unknown-host";
            }
            return host;
        }

        public String getAddresses() {
            String addresses;
            try {
                Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
                Set<String> ifList = new HashSet<>();
                while (ifs.hasMoreElements()) {
                    NetworkInterface ni = ifs.nextElement();
                    Enumeration<InetAddress> niAddresses = ni.getInetAddresses();
                    if (ni.isLoopback() || !ni.isUp()) {
                        continue;
                    }
                    while (niAddresses.hasMoreElements()) {
                        InetAddress addr = niAddresses.nextElement();
                        if (addr.isLoopbackAddress()) {
                            continue;
                        }
                        if (addr instanceof Inet4Address) {
                            ifList.add(addr.getHostAddress());
                        }
                    }
                }
                addresses = String.join(",", ifList);
            } catch (SocketException e) {
                addresses = "unknown-addresses";
            }
            return addresses;
        }
    }
}

