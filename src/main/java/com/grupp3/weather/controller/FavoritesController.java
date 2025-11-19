package com.grupp3.weather.controller;

import com.grupp3.weather.dto.PlaceDTO;
import com.grupp3.weather.mapper.PlaceMapper;
import com.grupp3.weather.model.Place;
import com.grupp3.weather.service.PlaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoritesController {

    private final PlaceService placeService;
    private final PlaceMapper placeMapper;

    public FavoritesController(PlaceService placeService, PlaceMapper placeMapper) {
        this.placeService = placeService;
        this.placeMapper = placeMapper;
    }

    @GetMapping
    public ResponseEntity<List<PlaceDTO>> getFavorites() {
        List<Place> favorites = placeService.findFavorites();
        return ResponseEntity.ok(placeMapper.toDTOList(favorites));
    }

    @PutMapping("/{placeName}")
    public ResponseEntity<PlaceDTO> addFavorite(@PathVariable String placeName) {
        Place place = placeService.setFavorite(placeName, true)
                .orElseThrow(() -> new RuntimeException("Place not found: " + placeName));
        return ResponseEntity.ok(placeMapper.toDTO(place));
    }

    @DeleteMapping("/{placeName}")
    public ResponseEntity<Void> removeFavorite(@PathVariable String placeName) {
        placeService.setFavorite(placeName, false);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/{placeName}")
    public ResponseEntity<Boolean> isFavorite(@PathVariable String placeName) {
        boolean isFav = placeService.findByName(placeName)
                .map(Place::isFavorite)
                .orElse(false);
        return ResponseEntity.ok(isFav);
    }
}
