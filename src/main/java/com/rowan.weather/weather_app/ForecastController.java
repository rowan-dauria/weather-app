package com.rowan.weather.weather_app;

import com.rowan.weather.weather_app.service.ForecastDataPOJO;
import com.rowan.weather.weather_app.service.ForecastService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;


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
    public @ResponseBody ForecastDataPOJO forecast(HttpServletRequest request, HttpServletResponse response) {
        String period = request.getParameter("period");
        if (!(ForecastQueryParams.getPeriods().contains(period))) {
            throw new InvalidForecastQueryException();
        }
        try {
            return service.dailyForecast();
        } catch (Exception e) {
            throw new ForecastServiceException(e);
        }
    }

}

abstract class ForecastQueryParams {
    @Getter private static final List<String> periods = List.of("hourly", "three-hourly", "daily");
}

@ResponseStatus(value = HttpStatus.BAD_GATEWAY, reason = "Exception in forecast service")
class ForecastServiceException extends RuntimeException {
    ForecastServiceException(Throwable e) {
        super("Exception was thrown by the forecast service", e);
    }
}

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Invalid forecast query parameters")
class InvalidForecastQueryException extends RuntimeException {}