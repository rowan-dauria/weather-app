package com.rowan.weather.weather_app.util;

import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = {String.class})
public class DailyForecastDataTests {
    // Use pre-packaged Spring Boot utils to read from resources
    // Reads files from folder under src/test/resources
    @Value("classpath:hourly-sample.json")
    private Resource hourlyResource;

    @Test
    void instantiatesClass() throws IOException {
        new DailyForecastData(hourlyResource.getContentAsString(StandardCharsets.UTF_8));
    }

    @Test
    void printResource() throws IOException {
        System.out.println(hourlyResource.getContentAsString(StandardCharsets.UTF_8));
    }

    @Test
    void assertTemperaturesNotNull() throws IOException {
        DailyForecastData forecastData = new DailyForecastData(hourlyResource.getContentAsString(StandardCharsets.UTF_8));
        assertThat(forecastData.getTemperatures()).isNotNull();
    }
}
