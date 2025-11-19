package com.grupp3.weather.controller;

import com.grupp3.weather.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final WeatherService weatherService;

    public AdminController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @PostMapping("/weather/update")
    public ResponseEntity<Map<String, String>> triggerWeatherUpdate() {
        log.info("Admin triggered manual weather update");
        return ResponseEntity.ok(Map.of(
            "message", "Weather update endpoint (manual trigger)", 
            "status", "Admin only - working!"
        ));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Admin accessed stats endpoint");
        return ResponseEntity.ok(Map.of(
            "message", "Admin stats endpoint",
            "status", "operational",
            "adminAccess", true
        ));
    }
}
