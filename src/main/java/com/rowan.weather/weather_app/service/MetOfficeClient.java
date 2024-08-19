package com.rowan.weather.weather_app.service;


import org.springframework.core.SpringProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MetOfficeClient {

    // https://data.hub.api.metoffice.gov.uk/sitespecific/v0/point/hourly?latitude=51.507351&longitude=-0.127758

    private final String apiKey;

    private final RestClient restClient;

    public MetOfficeClient(RestClient.Builder restClientBuilder) {
        apiKey = SpringProperties.getProperty("APIKEY");
        restClient = restClientBuilder
                .baseUrl("https://data.hub.api.metoffice.gov.uk")
                .defaultHeader("apikey", SpringProperties.getProperty("APIKEY"))
                .build();
    }

    public ForecastDataPOJO getSiteSpecificDailyForecast(final double latitude, final double longitude) {
        return new ForecastDataPOJO();
    }

    public ForecastDataPOJO getSiteSpecificThreeHourlyForecast(final double latitude, final double longitude) {
        return new ForecastDataPOJO();
    }

    public ForecastDataPOJO getSiteSpecificHourlyForecast(final double latitude, final double longitude) {
        return new ForecastDataPOJO();
    }
}
