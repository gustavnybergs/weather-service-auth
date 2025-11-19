package com.grupp3.weather.controller;

import com.grupp3.weather.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final WeatherService weatherService;

    public AdminController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @PostMapping("/weather/update")
    public ResponseEntity<Map<String, String>> triggerWeatherUpdate() {
        // TODO: Hook up to scheduled weather update when that method is refactored
        // For now just return confirmation that admin endpoint is working
        return ResponseEntity.ok(Map.of(
            "message", "Weather update endpoint (manual trigger)", 
            "status", "Admin only - working!"
        ));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        // Basic stats endpoint for admin dashboard
        return ResponseEntity.ok(Map.of(
            "message", "Admin stats endpoint",
            "status", "operational",
            "adminAccess", true
        ));
    }
}
