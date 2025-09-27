package mil.teng254.legacy.controller;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlCheckTest {
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
}

