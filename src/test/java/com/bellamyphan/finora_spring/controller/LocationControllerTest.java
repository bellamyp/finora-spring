package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.LocationCreateDto;
import com.bellamyphan.finora_spring.dto.LocationDto;
import com.bellamyphan.finora_spring.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationControllerTest {

    @Mock
    private LocationService locationService;

    @InjectMocks
    private LocationController controller;

    private LocationCreateDto createDto;
    private LocationDto locationDto;

    @BeforeEach
    void setUp() {
        // Input DTO for create
        createDto = new LocationCreateDto("New York", "NY");

        // Output DTO returned by service
        locationDto = new LocationDto("loc123", "New York", "NY");
    }

    @Test
    void createLocation_ReturnsLocationDto() {
        when(locationService.createLocation(createDto)).thenReturn(locationDto);

        ResponseEntity<LocationDto> response = controller.createLocation(createDto);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(locationDto);

        verify(locationService, times(1)).createLocation(createDto);
        verifyNoMoreInteractions(locationService);
    }

    @Test
    void getLocations_ReturnsListOfLocations() {
        List<LocationDto> locations = List.of(locationDto);
        when(locationService.getAllLocations()).thenReturn(locations);

        ResponseEntity<List<LocationDto>> response = controller.getLocations();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).containsExactly(locationDto);

        verify(locationService, times(1)).getAllLocations();
        verifyNoMoreInteractions(locationService);
    }
}
