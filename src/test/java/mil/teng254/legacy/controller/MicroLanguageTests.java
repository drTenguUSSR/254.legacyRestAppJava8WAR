package mil.teng254.legacy.controller;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String message="spec-symbols:\0,␀,\u2401,\u2426,\u274C,\u274E,\u2049";
        message+=",\u27B0"; // Curly Loop, '➰'
        message+=",\u00BF"; //Inverted Question Mark, '¿'
        //(UTF-16 Encoding), see https://www.compart.com/en/unicode/block/U+1F300
        message+=",\uD83D\uDE2D"; //Loudly Crying Face Emoji Meaning,😭
        message+=",\uD83D\uDC0D"; // Snake,🐍
        message+=",\uD83D\uDD25"; // Fire,1F525,'🔥'
        message+=",\uD83D\uDDD1"; // Wastebasket,1F5D1,'🗑'
        message+=",\u2622\ufe0f"; // Эмодзи Радиация, '☢'
        message+=",\u2623\ufe0f"; // Эмодзи Биологическая Угроза, '☣️'
        System.out.println(message);
    }
}

