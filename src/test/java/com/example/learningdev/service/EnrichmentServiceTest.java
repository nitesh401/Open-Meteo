package com.example.learningdev.service;

import com.example.learningdev.dto.GeocodingResponse;
import com.example.learningdev.dto.WeatherResponse;
import com.example.learningdev.model.EnrichedPost;
import com.example.learningdev.repository.EnrichedPostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnrichmentServiceTest {

    @Test
    void enrichAndSave_savesWeatherData() {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        EnrichedPostRepository repo = Mockito.mock(EnrichedPostRepository.class);
        ObjectMapper mapper = new ObjectMapper();

        String geocodeUrl = "http://geo?name={name}";
        String weatherUrl = "http://weather?latitude={lat}&longitude={lon}&current_weather=true";

        GeocodingResponse.GeoResult geo = new GeocodingResponse.GeoResult("London", 51.5072, -0.1276, "GB");
        GeocodingResponse geoResp = new GeocodingResponse(List.of(geo));
        Mockito.when(restTemplate.getForObject(geocodeUrl, GeocodingResponse.class, "London")).thenReturn(geoResp);

        WeatherResponse.CurrentWeather cw = new WeatherResponse.CurrentWeather(12.3, 4.5, 200.0, 3, "2026-09-19T12:00:00Z");
        WeatherResponse weatherResp = new WeatherResponse(cw);
        Mockito.when(restTemplate.getForObject(weatherUrl, WeatherResponse.class, 51.5072, -0.1276)).thenReturn(weatherResp);

        ArgumentCaptor<EnrichedPost> captor = ArgumentCaptor.forClass(EnrichedPost.class);
        Mockito.when(repo.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        EnrichmentService svc = new EnrichmentService(restTemplate, repo, mapper, geocodeUrl, weatherUrl);
        EnrichedPost saved = svc.enrichAndSave("London");

        assertEquals("London", saved.getLocation());
        assertEquals(51.5072, saved.getLatitude(), 0.0001);
        assertEquals(-0.1276, saved.getLongitude(), 0.0001);
        assertEquals(12.3, saved.getTemperature(), 0.0001);
        assertEquals(4.5, saved.getWindspeed(), 0.0001);
        assertEquals(200.0, saved.getWinddirection(), 0.0001);
        assertEquals(3, saved.getWeatherCode());
        assertNotNull(saved.getRawJson());

        Mockito.verify(repo).save(Mockito.any(EnrichedPost.class));
    }
}
