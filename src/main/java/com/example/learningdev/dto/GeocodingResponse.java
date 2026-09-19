package com.example.learningdev.dto;

import java.util.List;

public record GeocodingResponse(List<GeoResult> results) {
    public static record GeoResult(String name, double latitude, double longitude, String country) {}
}
