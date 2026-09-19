learningdev — Spring Boot example: fetch weather for a location and save to H2 (with simple UI)

Overview
--------
This sample Spring Boot application demonstrates resolving a location to coordinates (Open‑Meteo geocoding), fetching current weather (Open‑Meteo), transforming the response and persisting an enriched weather record into an in-memory H2 database. A lightweight static UI is included so the app is full-stack without a frontend framework.

Tech stack
----------
- Spring Boot 4.1.1
- Java 17
- Spring Web, Spring Data JPA, H2
- RestTemplate for HTTP calls
- Jackson (ObjectMapper) for JSON handling
- Vanilla JS + Bootstrap (static UI)

How it works (flow)
-------------------
1. Client (UI or API) POSTs to /api/enrich with { "location": "<city>" }.
2. Controller calls EnrichmentService.
3. Service calls external.geocode.url to get lat/lon, then external.weather.url to get current_weather.
4. Service serializes raw weather JSON, extracts key fields, builds EnrichedPost and saves via JPA.
5. Controller returns the saved entity (HTTP 201). UI lists saved records via GET /api/enriched.

Project structure
-----------------
src/main/java/com/example/learningdev
- LearningdevApplication.java                — Spring Boot entrypoint
- config/RestTemplateConfig.java             — RestTemplate bean
- config/JacksonConfig.java                  — ObjectMapper bean (JSR-310 enabled)
- controller/EnrichmentController.java       — POST /api/enrich, GET /api/enriched
- dto/EnrichRequest.java                     — request payload { location }
- dto/GeocodingResponse.java                 — geocoding DTOs
- dto/WeatherResponse.java                   — weather DTOs
- service/EnrichmentService.java             — coordinates lookup, weather fetch, persist
- model/EnrichedPost.java                    — JPA entity with weather fields
- repository/EnrichedPostRepository.java     — Spring Data JPA repository
- exception/GlobalExceptionHandler.java      — maps errors to HTTP responses

UI files (static)
-----------------
- src/main/resources/static/index.html       — simple Bootstrap + vanilla JS UI
  - Enter a location, click "Fetch & Save" to call POST /api/enrich
  - Saved records are shown (GET /api/enriched)

Configuration
-------------
File: src/main/resources/application.properties
- spring.datasource.* — H2 configuration (in-memory DB)
- external.geocode.url — e.g. https://geocoding-api.open-meteo.com/v1/search?name={name}
- external.weather.url — e.g. https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&current_weather=true

Note: Jackson ObjectMapper is exposed via config/JacksonConfig and registers JavaTimeModule so LocalDateTime is serialized correctly.

Build, test & run
-----------------
- mvn clean test    — runs unit tests (includes EnrichmentServiceTest using Mockito)
- mvn clean package
- mvn spring-boot:run
Or: java -jar target/learningdev-0.0.1-SNAPSHOT.jar

Accessing the app
-----------------
- UI: http://localhost:8080/  (static index.html)
- H2 console: http://localhost:8080/h2-console (jdbc:h2:mem:learningdb)

API: POST /api/enrich
---------------------
Request JSON:
{ "location": "London" }

Example curl:
curl -X POST -H "Content-Type: application/json" -d '{"location":"London"}' http://localhost:8080/api/enrich

GET /api/enriched
-----------------
Returns JSON array of saved EnrichedPost entities.

Responses
---------
- 201 Created: returns persisted EnrichedPost JSON (location, lat/lon, temperature, windspeed, winddirection, weatherCode, rawJson, processedAt)
- 502 Bad Gateway: when external APIs fail (mapped from IllegalStateException by GlobalExceptionHandler)
- 500 Internal Server Error: other unexpected errors

Notes on frontend choices
-------------------------
- A static vanilla-JS UI was added to avoid a build step and keep the project simple.
- For a richer SPA experience use React/Angular/Vue; I can scaffold a React app and integrate it (requires npm/yarn and a build step).

Tests
-----
- Unit: src/test/java/com/example/learningdev/service/EnrichmentServiceTest.java (uses Mockito to mock RestTemplate and repository)

Notes and next steps
--------------------
- Replace RestTemplate with WebClient for non-blocking calls if desired.
- Add integration tests (MockMvc/@SpringBootTest) and retry/resilience (Resilience4j).
- Consider moving configuration to a typed @ConfigurationProperties class.

Contact
-------
If you want, I can scaffold a React frontend, add integration tests, or improve error handling. Which should I do next?
