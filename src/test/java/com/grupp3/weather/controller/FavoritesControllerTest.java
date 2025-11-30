package com.grupp3.weather.controller;

import com.grupp3.weather.dto.PlaceDTO;
import com.grupp3.weather.mapper.PlaceMapper;
import com.grupp3.weather.model.Place;
import com.grupp3.weather.service.PlaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoritesControllerTest {

    @Mock
    private PlaceService placeService;

    @Mock
    private PlaceMapper placeMapper;

    @InjectMocks
    private FavoritesController favoritesController;

    private Place testPlace;
    private PlaceDTO testPlaceDTO;

    @BeforeEach
    void setUp() {
        testPlace = new Place();
        testPlace.setName("Stockholm");
        testPlace.setLatitude(59.3293);
        testPlace.setLongitude(18.0686);
        testPlace.setFavorite(false);

        testPlaceDTO = new PlaceDTO();
        testPlaceDTO.setName("Stockholm");
        testPlaceDTO.setLatitude(59.3293);
        testPlaceDTO.setLongitude(18.0686);
        testPlaceDTO.setFavorite(false);
    }

    @Test
    void testAddFavorite_Success() {
        testPlace.setFavorite(true);
        testPlaceDTO.setFavorite(true);

        when(placeService.setFavorite(eq("Stockholm"), eq(true)))
                .thenReturn(Optional.of(testPlace));
        when(placeMapper.toDTO(testPlace)).thenReturn(testPlaceDTO);

        ResponseEntity<PlaceDTO> response = favoritesController.addFavorite("Stockholm");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isFavorite());
        assertEquals("Stockholm", response.getBody().getName());

        verify(placeService).setFavorite("Stockholm", true);
        verify(placeMapper).toDTO(testPlace);
    }

    @Test
    void testAddFavorite_PlaceNotFound() {
        when(placeService.setFavorite(anyString(), eq(true)))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            favoritesController.addFavorite("NonExistent");
        });

        verify(placeService).setFavorite("NonExistent", true);
        verify(placeMapper, never()).toDTO(any());
    }

    @Test
    void testRemoveFavorite_Success() {
        when(placeService.setFavorite(eq("Stockholm"), eq(false)))
                .thenReturn(Optional.of(testPlace));

        ResponseEntity<Void> response = favoritesController.removeFavorite("Stockholm");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(placeService).setFavorite("Stockholm", false);
    }

    @Test
    void testGetFavorites_WithResults() {
        Place place1 = new Place();
        place1.setName("Stockholm");
        place1.setFavorite(true);

        Place place2 = new Place();
        place2.setName("Göteborg");
        place2.setFavorite(true);

        PlaceDTO dto1 = new PlaceDTO();
        dto1.setName("Stockholm");
        dto1.setFavorite(true);

        PlaceDTO dto2 = new PlaceDTO();
        dto2.setName("Göteborg");
        dto2.setFavorite(true);

        List<Place> favorites = Arrays.asList(place1, place2);
        List<PlaceDTO> favoriteDTOs = Arrays.asList(dto1, dto2);

        when(placeService.findFavorites()).thenReturn(favorites);
        when(placeMapper.toDTOList(favorites)).thenReturn(favoriteDTOs);

        ResponseEntity<List<PlaceDTO>> response = favoritesController.getFavorites();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().stream().allMatch(PlaceDTO::isFavorite));

        verify(placeService).findFavorites();
        verify(placeMapper).toDTOList(favorites);
    }

    @Test
    void testGetFavorites_EmptyList() {
        when(placeService.findFavorites()).thenReturn(Arrays.asList());
        when(placeMapper.toDTOList(anyList())).thenReturn(Arrays.asList());

        ResponseEntity<List<PlaceDTO>> response = favoritesController.getFavorites();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(placeService).findFavorites();
    }

    @Test
    void testIsFavorite_True() {
        testPlace.setFavorite(true);

        when(placeService.findByName("Stockholm"))
                .thenReturn(Optional.of(testPlace));

        ResponseEntity<Boolean> response = favoritesController.isFavorite("Stockholm");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody());

        verify(placeService).findByName("Stockholm");
    }

    @Test
    void testIsFavorite_False() {
        testPlace.setFavorite(false);

        when(placeService.findByName("Stockholm"))
                .thenReturn(Optional.of(testPlace));

        ResponseEntity<Boolean> response = favoritesController.isFavorite("Stockholm");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody());

        verify(placeService).findByName("Stockholm");
    }

    @Test
    void testIsFavorite_PlaceNotFound() {
        when(placeService.findByName("NonExistent"))
                .thenReturn(Optional.empty());

        ResponseEntity<Boolean> response = favoritesController.isFavorite("NonExistent");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody());

        verify(placeService).findByName("NonExistent");
    }
}
