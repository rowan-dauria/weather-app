package com.rowan.weather.weather_app;

import com.rowan.weather.weather_app.service.ForecastDataPOJO;
import com.rowan.weather.weather_app.service.MetOfficeClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Needed to make @beforeall not static
@RestClientTest(MetOfficeClient.class)
public class MetOfficeClientTests {
    @Autowired
    private MetOfficeClient client;

    @Autowired
    private MockRestServiceServer mockServer;

    @BeforeAll
    void setup() throws IOException {

        byte[] bytes = Files.readAllBytes(Paths.get("C:\\Users\\Rowan d'Auria\\Documents\\GitHub\\weather-app\\src\\test\\java\\com\\rowan\\weather\\weather_app\\dummy_data\\hourly-sample.json"));
        mockServer.expect(requestTo("sitespecific/v0/point/hourly?latitude=51.507351&longitude=-0.127758"))
                .andRespond(withSuccess(bytes, MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldHaveMethodsByPeriod() {
        ForecastDataPOJO dailyForecast = this.client.getSiteSpecificDailyForecast(51.507351, -0.127758);
        assertThat(dailyForecast.getData()).isEqualTo(5);

        ForecastDataPOJO threeHourlyForecast = this.client.getSiteSpecificThreeHourlyForecast(51.507351, -0.127758);
        assertThat(dailyForecast.getData()).isEqualTo(5);

        ForecastDataPOJO hourlyForecast = this.client.getSiteSpecificHourlyForecast(51.507351, -0.127758);
        assertThat(dailyForecast.getData()).isEqualTo(5);
    }
}
