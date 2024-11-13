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
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

        String slowURI = String.format(
                "https://data.hub.api.metoffice.gov.uk/sitespecific/v0/point/hourly?latitude=%f&longitude=%f",
                0.000000,
                0.000000
        );
        // todo move this mockserver expect so that it is test dependent
        mockServer.expect(ExpectedCount.once(), requestTo(slowURI))
                .andRespond(req -> {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return withSuccess("{ \"message\": \"ok\" }", MediaType.APPLICATION_JSON).createResponse(req);
                });
        mockServer.expect(ExpectedCount.once(), requestTo(requestURI))
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

    @Test
    void shouldBeNonBlockingForSlowServer() {
        StopWatch stopWatch_slow = new StopWatch("slow");
        StopWatch stopWatch_fast = new StopWatch("fast");

        CompletableFuture<Long> duration_slow = CompletableFuture.supplyAsync(() -> {
            stopWatch_slow.start();
            System.out.println("stopwatch started for slow request");
            // Specific coords cause a slow response
            this.client.getSiteSpecificHourlyForecast(0.000000, 0.000000);
            stopWatch_slow.stop();
            System.out.println("stopwatch stopped for slow request");
            return stopWatch_slow.getTotalTimeMillis();
        });

        CompletableFuture<Long> duration_fast = CompletableFuture.supplyAsync(() -> {
            stopWatch_fast.start();
            System.out.println("stopwatch started for fast request");
            this.client.getSiteSpecificHourlyForecast(coords[0], coords[1]);
            stopWatch_fast.stop();
            System.out.println("stopwatch stopped for fast request");
            return stopWatch_fast.getTotalTimeMillis();
        });

        CompletableFuture<Void> combined = CompletableFuture.allOf(duration_slow, duration_fast);

        combined.join();

        try {
            System.out.printf("Duration fast request: %d milliseconds", duration_fast.get());

            assertThat(duration_slow.get()).isGreaterThanOrEqualTo(5000);
            assertThat(duration_fast.get()).isLessThan(1000);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
