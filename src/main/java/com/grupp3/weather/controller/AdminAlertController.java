package com.grupp3.weather.controller;

import com.grupp3.weather.model.WeatherAlert;
import com.grupp3.weather.repository.WeatherAlertRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/alerts")
@PreAuthorize("hasRole('ADMIN')")  // ← NYTT: Hela controllern kräver ADMIN
public class AdminAlertController {

    private final WeatherAlertRepository alertRepository;

    public AdminAlertController(WeatherAlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @PostMapping
    public ResponseEntity<WeatherAlert> createAlert(@RequestBody WeatherAlert alert) {
        WeatherAlert saved = alertRepository.save(alert);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<WeatherAlert>> getAllAlerts() {
        return ResponseEntity.ok(alertRepository.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WeatherAlert> updateAlert(@PathVariable Long id, @RequestBody WeatherAlert alert) {
        if (!alertRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        alert.setId(id);
        return ResponseEntity.ok(alertRepository.save(alert));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        if (!alertRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        alertRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
