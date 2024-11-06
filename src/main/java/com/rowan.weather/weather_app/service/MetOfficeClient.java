package com.rowan.weather.weather_app.service;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import jakarta.annotation.PostConstruct;

@Service
public class MetOfficeClient {

    // https://data.hub.api.metoffice.gov.uk/sitespecific/v0/point/hourly?latitude=51.507351&longitude=-0.127758

    private RestClient restClient;

    private final MetClientConfig metClientConfig;

    @Autowired
    public MetOfficeClient(MetClientConfig metClientConfig) {
        this.metClientConfig = metClientConfig;
    }

    @PostConstruct
    public void init() {
        System.out.println(metClientConfig.getApiKey());
        restClient = RestClient.builder()
                .baseUrl("https://data.hub.api.metoffice.gov.uk/")
                .defaultHeader("apikey", metClientConfig.getApiKey())
                .build();
    }

    public ForecastDataPOJO getSiteSpecificDailyForecast(final double latitude, final double longitude) {
        return new ForecastDataPOJO();
    }

    public ForecastDataPOJO getSiteSpecificThreeHourlyForecast(final double latitude, final double longitude) {
        return new ForecastDataPOJO();
    }

    public String getSiteSpecificHourlyForecast(final double latitude, final double longitude) {
        String uri = String.format("sitespecific/v0/point/hourly?latitude=%f1&longitude=%f", latitude, longitude);
        try {
            return restClient
                    .get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return "error";
    }
}

