package com.rowan.weather.weather_app;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rowan.weather.weather_app.service.ForecastDataPOJO;
import com.rowan.weather.weather_app.service.ForecastService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Assert;

@SpringBootTest
@AutoConfigureMockMvc

public class MockMvcTests {

    @MockBean
    private ForecastService service;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void shouldReturnDefaultMessage() throws Exception {
        when(service.greet()).thenReturn("Hello Mock");

        this.mockMvc.perform(get("/hello")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello Mock")));
    }

    @Test
    void shouldReturnDailyForecast() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/forecast").param("period", "daily"))
                .andExpectAll(
                    status().isOk(),
                    content().contentType("application/json")
        ).andReturn();

        String responseData = result.getResponse().getContentAsString();

        ForecastDataPOJO response = objectMapper.readValue(responseData, ForecastDataPOJO.class);

        Assert.isTrue(response.getData() == 5, "data in object should equal 5");
    }

}
