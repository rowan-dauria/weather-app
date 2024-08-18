package com.rowan.weather.weather_app.service;


import org.springframework.core.SpringProperties;
import org.springframework.web.reactive.function.client.WebClient;

class MetOfficeClient {

    private final String apiKey;

    public MetOfficeClient() {
        this.apiKey = SpringProperties.getProperty("APIKEY");
    }

    public WebClient client() {
        return WebClient.create("https://data.hub.api.metoffice.gov.uk/");
    }
}
