package com.grupp3.weather.controller;

import com.grupp3.weather.dto.PlaceDTO;
import com.grupp3.weather.mapper.PlaceMapper;
import com.grupp3.weather.model.Place;
import com.grupp3.weather.service.PlaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/places")
public class PlaceController {

    private final PlaceService placeService;
    private final PlaceMapper placeMapper;

    public PlaceController(PlaceService placeService, PlaceMapper placeMapper) {
        this.placeService = placeService;
        this.placeMapper = placeMapper;
    }

    @GetMapping
    public ResponseEntity<List<PlaceDTO>> getAllPlaces() {
        List<Place> places = placeService.findAll();
        return ResponseEntity.ok(placeMapper.toDTOList(places));
    }
}
