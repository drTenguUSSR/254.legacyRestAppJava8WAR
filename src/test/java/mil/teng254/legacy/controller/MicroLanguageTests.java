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
        String message = "";
        message += ",\u0000"; // U+0000, <Null> (NUL), ''
        message += ",\u2400"; // U+2400, Symbol For Null, '␀'
        message += ",\u2401"; // U+2401, Symbol For Start of Heading '␁'
        message += ",\u27B0"; // Curly Loop, '➰'
        //(UTF-16 Encoding), see https://www.compart.com/en/unicode/block/U+1F300
        message += ",\uD83D\uDE2D"; //Loudly Crying Face Emoji Meaning,😭
        message += ",\uD83D\uDC0D"; // Snake,🐍
        message += ",\uD83D\uDD25"; // U+1F525, Fire, '🔥'
        message += ",\uD83D\uDDD1"; // U+1F5D1, Wastebasket, '🗑'
        message += ",\u2622\uFE0F"; // U+2622, Radioactive Sign (colored), '☢'
        message += ",\u2622"; // Radioactive Sign, '☢'
        message += ",\u2623\uFE0F"; // U+2623, Biohazard Sign (colored), '☣️'
        log.debug("spec-symbols-1:{}", message);
        message = "";

        //https://www.compart.com/en/unicode/block/U+2700
        //http://xahlee.info/comp/unicode_crosses.html
        message += ",\u2705"; // WHITE HEAVY CHECK MARK, '✅'
        message += ",\u274E"; // U+274E, Negative Squared Cross Mark, '❎'
        message += ",\u274C"; // U+274C, Cross Mark, '❌'
        message += ",\u2B55"; // U+2B55, HEAVY LARGE CIRCLE, '⭕'
        message += ",\u2620"; // U+2620, Skull and Crossbones, '☠'
        message += ",\u26A1"; // U+26A1, High Voltage Sign, '⚡'
        message += ",\u26D4"; // U+26D4, No Entry, '⛔'
        message += ",\u26CF"; // U+26CF, Pick, '⛏'
        log.debug("spec-symbols-2:{}", message);
        message = "";

        message += ",\uD83D\uDD96"; // U+1F596, вулканский салют, '🖖'
        message += ",\u00BF"; // U+BF, INVERTED QUESTION MARK, '¿'
        message += ",\u2049"; // U+2049, Exclamation Question Mark, '⁉'
        message += ",\u2426"; // U+2426, Symbol For Substitute Form Two, '␦'
        message += ",\u203D"; // U+203D, Interrobang, '‽'
        message += ",\u26A0"; // U+26A0, Warning Sign, ⚠
        message += ",\uD83D\uDD17"; // U+1F517, LINK SYMBOL, '🔗'
        message += ",\u2693"; // U+2693, ANCHOR, '⚓'
        log.debug("spec-symbols-3:{}", message);
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

