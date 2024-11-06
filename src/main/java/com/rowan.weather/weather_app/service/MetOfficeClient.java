package com.rowan.weather.weather_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MetOfficeClient {

    // https://data.hub.api.metoffice.gov.uk/sitespecific/v0/point/hourly?latitude=51.507351&longitude=-0.127758

    private final RestClient restClient;

    private final MetClientConfig metClientConfig;

    // Cannot add the api key as a default header because the config properties have not been set at
    // the point of client instantiation. Additionally, if the configuration of restClient is put in a
    // @PostConstruct method, the MockRestServiceServer in the test class cannot be autoconfigured because
    // Spring Boot expects the RestClient to be injected in the constructor.
    @Autowired
    public MetOfficeClient(MetClientConfig config, RestClient.Builder builder) {
        metClientConfig = config;
        restClient = builder
                .baseUrl("https://data.hub.api.metoffice.gov.uk/")
                .build();
    }

    public ForecastDataPOJO getSiteSpecificDailyForecast(final double latitude, final double longitude) {
        return new ForecastDataPOJO();
    }

    public ForecastDataPOJO getSiteSpecificThreeHourlyForecast(final double latitude, final double longitude) {
        return new ForecastDataPOJO();
    }

    public String getSiteSpecificHourlyForecast(final double latitude, final double longitude) {
        String uri = String.format("sitespecific/v0/point/hourly?latitude=%f&longitude=%f", latitude, longitude);
        try {
            return restClient
                    .get()
                    .uri(uri)
                    .header("apikey", metClientConfig.getApiKey())
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return "error";
    }
}

