package com.grupp3.weather.mapper;

import com.grupp3.weather.dto.PlaceDTO;
import com.grupp3.weather.model.Place;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PlaceMapper {

    public PlaceDTO toDTO(Place place) {
        if (place == null) {
            return null;
        }
        
        return new PlaceDTO(
            place.getId(),
            place.getDisplayName(),
            place.getLat(),
            place.getLon(),
            place.isFavorite()
        );
    }

    public List<PlaceDTO> toDTOList(List<Place> places) {
        return places.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Place toEntity(PlaceDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Place place = new Place();
        place.setId(dto.getId());
        place.setDisplayName(dto.getDisplayName());
        place.setLat(dto.getLat());
        place.setLon(dto.getLon());
        place.setFavorite(dto.isFavorite());
        
        return place;
    }
}
