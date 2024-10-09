package com.rowan.weather.weather_app;

import com.rowan.weather.weather_app.service.ForecastDataPOJO;
import com.rowan.weather.weather_app.service.MetOfficeClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Needed to make @beforeall not static
@RestClientTest(MetOfficeClient.class)
public class MetOfficeClientTests {
    @Autowired
    private MetOfficeClient client;

    @Autowired
    private MockRestServiceServer mockServer;

    @BeforeAll
    void setup() throws IOException {

    }
    // TODO Make the service implement the request to the metoffice API so that the metoffice server can be mocked
    // TODO Make the mock server read the data from a dummy JSON file taken from metoffice website. This will enable me to make an appropriate POJO
    @Test
    void shouldHaveMethodsByPeriod() {
        ForecastDataPOJO dailyForecast = this.client.getSiteSpecificDailyForecast(51.507351, -0.127758);
        assertThat(dailyForecast.getData()).isEqualTo(5);

        ForecastDataPOJO threeHourlyForecast = this.client.getSiteSpecificThreeHourlyForecast(51.507351, -0.127758);
        assertThat(threeHourlyForecast.getData()).isEqualTo(5);

        ForecastDataPOJO hourlyForecast = this.client.getSiteSpecificHourlyForecast(51.507351, -0.127758);
        assertThat(hourlyForecast.getData()).isEqualTo(5);
    }
}
