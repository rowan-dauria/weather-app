package com.rowan.weather.weather_app.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties("metofficeclient")
public class MetClientConfig {
    private String apiKey;
}
