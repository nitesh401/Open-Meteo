package com.example.learningdev.dto;

public record WeatherResponse(CurrentWeather current_weather) {
    public static record CurrentWeather(Double temperature, Double windspeed, Double winddirection, Integer weathercode, String time) {}
}
