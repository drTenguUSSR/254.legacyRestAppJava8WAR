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

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    private final int index;
    private final String srcData;
    private final String stamp; // второй параметр, если нужен
    private final int expectedResult;

    public SpecialMarkIT(int index, String srcData, String stamp, int expectedResult) {
        this.index = index;
        this.srcData = srcData;
        this.stamp = stamp;
        this.expectedResult = expectedResult;
    }

    //!index!;srcJson;url;type:M(main),S(special);httpCode:200,403;expectedKey
    @Parameterized.Parameters(name = "Test #{0}: srcData={1}, stamp={2}, expectedResult={3}")
    public static Collection<Object[]> testData() {
        Object[][] rawData = new Object[][]{
                {"{\"key\": 100, \"stamp\":\"2025-06-20T11:24:36Z\"}", "2025-06-20T11:24:36Z", 101},
                {"{\"key\": 200, \"stamp\":\"2025-06-21T11:24:36Z\"}", "2025-06-21T11:24:36Z", 201},
                {"{\"key\": 300, \"stamp\":\"2025-06-22T11:24:36Z\"}", "2025-06-22T11:24:36Z", 301}
        };
        List<Object[]> data = new ArrayList<>();
        for (int i = 0; i < rawData.length; i++) {
            data.add(new Object[]{i, rawData[i][0], rawData[i][1], rawData[i][2]});
        }
        return data;
    }

    @Test
    public void FunA() {
        log.debug("work: index: {}, key: {}, val: {}", index, srcData, expectedResult);
        WebResource webResource = specialResource().path("/special-bravo/mark");

        ClientResponse response = webResource
                .header(StaticHolder.HTTP_HEADER_TEST_ID, getTestId())
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, srcData);

        Assert.assertEquals(200, response.getStatus());
        PublicDtos.ResponseCommon respData = response.getEntity(PublicDtos.ResponseCommon.class);
        Assert.assertEquals(Long.valueOf(expectedResult), respData.getRes());
    }

    //@Test
    public void FunB() {
        log.debug("FunB-BEG");
        WebResource webResource = mainResource().path("/special-bravo/mark");

        String srcData = "{\"key\": 100, \"stamp\":\"2025-06-20T11:24:36Z\"}";
        ClientResponse response = webResource
                .header(StaticHolder.HTTP_HEADER_TEST_ID, getTestId())
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, srcData);

        Assert.assertEquals(403, response.getStatus());
//        PublicDtos.ResponseCommon respData = response.getEntity(PublicDtos.ResponseCommon.class);
//        Assert.assertEquals(Long.valueOf(101), respData.getRes());
        log.debug("FunB-END2");
    }

//    @Test
//    public void multi() {
//
//    }
}
