package com.rowan.weather.weather_app;

import com.rowan.weather.weather_app.service.ForecastDataPOJO;
import com.rowan.weather.weather_app.service.MetClientConfig;
import com.rowan.weather.weather_app.service.MetOfficeClient;
import jakarta.annotation.PostConstruct;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Needed to make @beforeall not static
// Needed to include MetClientConfig in test context
@ContextConfiguration(classes = {MetOfficeClient.class, MetClientConfig.class})
@RestClientTest(MetOfficeClient.class)
public class MetOfficeClientTests {
    @Autowired
    private MetOfficeClient client;

    @Autowired
    private MockRestServiceServer mockServer;

    // Use pre-packaged Spring Boot utils to read from resources
    // Reads files from folder under src/test/resources
    @Value("classpath:hourly-sample.json")
    private Resource hourlyResource;

    private final Double[] coords = {51.507351, -0.127758};

    public static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @BeforeAll
    void setup() {
        String requestURI = String.format(
                "https://data.hub.api.metoffice.gov.uk/sitespecific/v0/point/hourly?latitude=%f&longitude=%f",
                coords[0],
                coords[1]
        );
        // todo move this mockserver expect so that it is test dependent
        mockServer.expect(ExpectedCount.times(1), requestTo(requestURI))
                .andRespond(withSuccess(asString(hourlyResource), MediaType.APPLICATION_JSON));
    }
    @Test
    void checkMethodsExist() {
        Method dailyMethod = null;
        Method threeHourlyMethod = null;
        Method hourlyMethod = null;
        try {
            dailyMethod = client.getClass().getMethod("getSiteSpecificDailyForecast", double.class, double.class);
        } catch (NoSuchMethodException e) {
            System.err.println(e.getMessage());
        }
        try {
            threeHourlyMethod = client.getClass().getMethod("getSiteSpecificDailyForecast", double.class, double.class);
        } catch (NoSuchMethodException e) {
            System.err.println(e.getMessage());
        }
        try {
            hourlyMethod = client.getClass().getMethod("getSiteSpecificDailyForecast", double.class, double.class);
        } catch (NoSuchMethodException e) {
            System.err.println(e.getMessage());
        }

        assertThat(dailyMethod).isNotNull();
        assertThat(threeHourlyMethod).isNotNull();
        assertThat(hourlyMethod).isNotNull();
    }
    // TODO Make the service implement the request to the metoffice API so that the metoffice server can be mocked
    // TODO Make the mock server read the data from a dummy JSON file taken from metoffice website. This will enable me to make an appropriate POJO
    @Test
    void shouldMakeRequestToServer() {
        ForecastDataPOJO dailyForecast = this.client.getSiteSpecificDailyForecast(coords[0], coords[1]);
        assertThat(dailyForecast.getData()).isEqualTo(5);

        ForecastDataPOJO threeHourlyForecast = this.client.getSiteSpecificThreeHourlyForecast(51.507351, -0.127758);
        assertThat(threeHourlyForecast.getData()).isEqualTo(5);

        String hourlyForecast = this.client.getSiteSpecificHourlyForecast(coords[0], coords[1]);
        try {
            final JSONObject foreCastObj = new JSONObject(hourlyForecast);
            final String type = foreCastObj.getString("type");
            assertThat(type).isEqualTo("FeatureCollection");
        } catch (JSONException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("There was an error reading the hourly forecast JSON");
        } catch (NullPointerException e) {
            throw new RuntimeException("Could not find property 'type' in JSON object");
        }
    }
}
