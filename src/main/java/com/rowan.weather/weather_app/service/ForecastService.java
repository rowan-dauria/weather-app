package com.rowan.weather.weather_app.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class ForecastService {

    private MetOfficeClient metOfficeClient;

    ForecastService(MetOfficeClient client) {
        metOfficeClient = client;
    }

    public String greet() {
        return "Hello World";
    }

    // todo make put the forecast in a more specific data class
    public String hourlyForecast() {
        return metOfficeClient.getSiteSpecificHourlyForecast(51.507351, -0.127758);
    }

    public ForecastDataPOJO dailyForecast() {
        return metOfficeClient.getSiteSpecificThreeHourlyForecast(51.507351, -0.127758);
    }

    public ForecastDataPOJO threeHourlyForeCast() {
        return metOfficeClient.getSiteSpecificDailyForecast(51.507351, -0.127758);
    }
}
