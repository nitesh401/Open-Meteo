package com.example.learningdev.service;

import com.example.learningdev.dto.GeocodingResponse;
import com.example.learningdev.dto.WeatherResponse;
import com.example.learningdev.model.EnrichedPost;
import com.example.learningdev.repository.EnrichedPostRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;

/**
 * Service that resolves a location to coordinates, fetches current weather from the configured
 * external weather provider, transforms the response and persists an EnrichedPost entity.
 *
 * Design notes:
 * - Uses RestTemplate (configurable endpoints in application.properties)
 * - Serializes the raw weather response into rawJson for later inspection
 */
@Service
public class EnrichmentService {

    private final RestTemplate restTemplate;
    private final EnrichedPostRepository repository;
    private final String geocodeUrl;
    private final String weatherUrl;
    private final ObjectMapper objectMapper;

    public EnrichmentService(RestTemplate restTemplate,
                             EnrichedPostRepository repository,
                             ObjectMapper objectMapper,
                             @Value("${external.geocode.url}") String geocodeUrl,
                             @Value("${external.weather.url}") String weatherUrl) {
        this.restTemplate = restTemplate;
        this.repository = repository;
        this.geocodeUrl = geocodeUrl;
        this.weatherUrl = weatherUrl;
        this.objectMapper = objectMapper;
    }

    public EnrichedPost enrichAndSave(String location) {
        // 1) Resolve location -> lat/lon
        GeocodingResponse geo = restTemplate.getForObject(geocodeUrl, GeocodingResponse.class, location);
        if (geo == null || geo.results() == null || geo.results().isEmpty()) {
            throw new IllegalStateException("Geocoding API returned no results for location=" + location);
        }

        GeocodingResponse.GeoResult best = geo.results().get(0);
        double lat = best.latitude();
        double lon = best.longitude();

        // 2) Fetch weather for lat/lon
        WeatherResponse weather = restTemplate.getForObject(weatherUrl, WeatherResponse.class, lat, lon);
        if (weather == null || weather.current_weather() == null) {
            throw new IllegalStateException("Weather API returned no data for location=" + location);
        }

        WeatherResponse.CurrentWeather cw = weather.current_weather();

        String rawJson;
        try {
            rawJson = objectMapper.writeValueAsString(weather);
        } catch (JsonProcessingException e) {
            rawJson = "{}";
        }

        EnrichedPost entity = new EnrichedPost(
                location,
                lat,
                lon,
                cw.temperature(),
                cw.windspeed(),
                cw.winddirection(),
                cw.weathercode(),
                rawJson,
                LocalDateTime.now()
        );

        return repository.save(entity);
    }
}
