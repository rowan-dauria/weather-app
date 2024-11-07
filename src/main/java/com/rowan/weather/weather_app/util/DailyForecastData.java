package com.rowan.weather.weather_app.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DailyForecastData implements ForecastData {

    private final ArrayList<Float> temperatures = new ArrayList<>();

    DailyForecastData(String rawDataString) {
        JSONObject rawData = ForecastData.convertToJSONObj(rawDataString);
        transform(rawData);
    }
    @Override
    public void validate() throws ForecastDataValidationException {
        return;
    }

    @Override
    public void transform(JSONObject data) {
        JSONArray arr = data.getJSONArray("features")
                .getJSONObject(0)
                .getJSONObject("properties")
                .getJSONArray("timeSeries");

        Stream<JSONObject> stream = IntStream.range(0, arr.length())
                .mapToObj(arr::getJSONObject);

        stream.forEach(obj -> {
            float t = obj.getFloat("screenTemperature");
            temperatures.add(t);
        });

    }

    @Override
    public ArrayList<Float> getTemperatures() {
        return temperatures;
    }
}
