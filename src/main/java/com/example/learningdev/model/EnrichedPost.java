package com.example.learningdev.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA entity that stores enriched weather data for a location.
 *
 * It keeps the resolved coordinates, a few key current-weather fields and the full raw JSON
 * returned by the weather provider to allow later analysis or re-processing.
 */
@Entity
@Table(name = "enriched_posts")
public class EnrichedPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;

    private Double latitude;

    private Double longitude;

    private Double temperature;

    private Double windspeed;

    private Double winddirection;

    private Integer weatherCode;

    @Column(length = 5000)
    private String rawJson;

    private LocalDateTime processedAt;

    public EnrichedPost() {
    }

    public EnrichedPost(String location, Double latitude, Double longitude, Double temperature, Double windspeed, Double winddirection, Integer weatherCode, String rawJson, LocalDateTime processedAt) {
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.temperature = temperature;
        this.windspeed = windspeed;
        this.winddirection = winddirection;
        this.weatherCode = weatherCode;
        this.rawJson = rawJson;
        this.processedAt = processedAt;
    }

    public Long getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getWindspeed() {
        return windspeed;
    }

    public void setWindspeed(Double windspeed) {
        this.windspeed = windspeed;
    }

    public Double getWinddirection() {
        return winddirection;
    }

    public void setWinddirection(Double winddirection) {
        this.winddirection = winddirection;
    }

    public Integer getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(Integer weatherCode) {
        this.weatherCode = weatherCode;
    }

    public String getRawJson() {
        return rawJson;
    }

    public void setRawJson(String rawJson) {
        this.rawJson = rawJson;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}
