package com.rowan.weather.weather_app;

import com.rowan.weather.weather_app.service.ForecastDataPOJO;
import com.rowan.weather.weather_app.service.ForecastService;
import netscape.javascript.JSObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class ForecastController {

    private final ForecastService service;

    public ForecastController(ForecastService service) {
        this.service = service;
    }

    @GetMapping("/hello")
    public @ResponseBody String greeting() {
        return service.greet();
    }


    @GetMapping("/forecast")
    public @ResponseBody ForecastDataPOJO forecast() {
        return new ForecastDataPOJO();
    }

}