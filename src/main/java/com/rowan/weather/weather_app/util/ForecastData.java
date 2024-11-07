package com.rowan.weather.weather_app.util;

import org.json.JSONObject;

import java.util.ArrayList;

public interface ForecastData {

    void validate() throws ForecastDataValidationException;

    void transform(JSONObject data);

    static JSONObject convertToJSONObj(String dataString) {
        return new JSONObject(dataString);
    }

    ArrayList<Float> getTemperatures();

    class ForecastDataValidationException extends RuntimeException {
        ForecastDataValidationException() {
            super("There was an error validating the input data");
        }
    }
}
