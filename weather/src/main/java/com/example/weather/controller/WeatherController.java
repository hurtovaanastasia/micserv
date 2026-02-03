package com.example.weather.controller;

import com.example.weather.cache.WeatherCache;
import com.example.weather.model.Main;
import com.example.weather.model.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
public class WeatherController {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${appid}")
    private String appId;
    @Value("${url.weather}")
    private String urlWeather;
    @Autowired
    private WeatherCache weatherCache;

    @GetMapping("/weather")
    public Root getWeather(@RequestParam String lat, @RequestParam String lon) {
        String cacheKey = lat + "-" + lon;
        Root cachedData = weatherCache.get(cacheKey);
        if (cachedData != null) {
            return cachedData;
        }
        String request = String.format("%s?lat=%s&lon=%s&units=metric&appid=%s",
                urlWeather, lat, lon, appId);

        Root freshData = restTemplate.getForObject(request, Root.class);
        if (freshData != null) {
            weatherCache.putToCache(cacheKey, freshData);
        }
        return freshData;
    }
}
