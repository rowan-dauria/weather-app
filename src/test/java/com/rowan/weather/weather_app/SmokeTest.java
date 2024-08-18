package com.rowan.weather.weather_app;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SmokeTest {

    @Autowired
    private ForecastController controller;

    // Test that controller is being created
    @Test
    void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
    }
}
