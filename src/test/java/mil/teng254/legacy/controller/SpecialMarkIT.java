package mil.teng254.legacy.controller;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import lombok.extern.slf4j.Slf4j;
import mil.teng254.legacy.test.util.BasicIntegrationTest;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
@ContextConfiguration(locations = "classpath:test-applicationContext.xml")
@WebAppConfiguration
@Slf4j
public class SpecialMarkIT extends BasicIntegrationTest {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
    private static final String TEMPLATE_DATA = "{\"key\": {}, \"stamp\":\"2025-06-20T11:24:36Z\"}";
    private static final String URI_BRAVO = "/special-bravo/mark";
    private static final String URI_DELTA = "/special-delta/mark";

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    private final int index;
    private final int total;
    private final String srcData;
    private final String uri;
    private final ResourcePort type;
    private final int expectedHttp;
    private final int expectedKey;

    public SpecialMarkIT(int index, int total, String srcData, String uri, ResourcePort type, int expectedHttp, int expectedKey) {
        this.index = index;
        this.total = total;
        this.srcData = srcData;
        this.uri = uri;
        this.type = type;
        this.expectedHttp = expectedHttp;
        this.expectedKey = expectedKey;
    }

    @Parameterized.Parameters(name = "{0}/{1}: srcData={2}")
    public static Collection<Object[]> testData() {
        Object[][] rawData = new Object[][]{
                //srcJson;url;type:M(main),S(special);httpCode:200,403;expectedKey|-1
                {MessageFormatter.format(TEMPLATE_DATA, 20).getMessage(), URI_BRAVO, ResourcePort.SPECIAL, 200, 21}
                , {MessageFormatter.format(TEMPLATE_DATA, 23).getMessage(), URI_BRAVO, ResourcePort.MAIN, 403, -1}
                , {MessageFormatter.format(TEMPLATE_DATA, 110).getMessage(), URI_DELTA, ResourcePort.SPECIAL, 200, 117}
                , {MessageFormatter.format(TEMPLATE_DATA, 113).getMessage(), URI_DELTA, ResourcePort.MAIN, 403, -1}
        };

        int totalTests = rawData.length;
        List<Object[]> data = new ArrayList<>(totalTests);
        for (int i = 0; i < rawData.length; i++) {
            data.add(new Object[]{i + 1, totalTests, rawData[i][0], rawData[i][1], rawData[i][2], rawData[i][3], rawData[i][4]});
        }
        return data;
    }

    @Test
    public void funA() {
        log.debug("funA: {}/{}: {}, {}->{}\n{}", index, total, type + "/" + uri, expectedHttp, expectedKey, srcData);
        WebResource webResource;
        if (type == ResourcePort.SPECIAL) {
            webResource = specialResource().path(uri);
        } else if (type == ResourcePort.MAIN) {
            webResource = mainResource().path(uri);
        } else {
            throw new IllegalArgumentException("Unexpected type=" + type);
        }

        ClientResponse response = webResource
                .header(StaticHolder.HTTP_HEADER_TEST_ID, getTestId())
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, srcData);

        int respStatus = response.getStatus();
        if (respStatus != 200) {
            String contentType = response.getHeaders().getFirst("Content-Type");
            log.debug("resp check. respStatus={} contentType={}", respStatus, contentType);
        }
        Assert.assertEquals(expectedHttp, respStatus);
        if (respStatus == 200) {
            PublicDtos.ResponseCommon respData = response.getEntity(PublicDtos.ResponseCommon.class);
            Assert.assertEquals(Long.valueOf(expectedKey), respData.getRes());
        }
    }

    public enum ResourcePort {MAIN, SPECIAL}
}
