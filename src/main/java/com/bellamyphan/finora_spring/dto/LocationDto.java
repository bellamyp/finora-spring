package com.bellamyphan.finora_spring.dto;

import com.bellamyphan.finora_spring.entity.Location;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
public class LocationDto {

    @NotBlank(message = "Location id is required")
    private String id;

    @NotBlank(message = "City name is required")
    private String city;

    @NotBlank(message = "State name is required")
    private String state;

    // ==========================
    // Convert Entity → DTO
    // ==========================
    public static LocationDto fromEntity(Location location) {
        return Optional.ofNullable(location)
                .map(l -> new LocationDto(l.getId(), l.getCity(), l.getState()))
                .orElse(null);
    }

    // ==========================
    // Convert DTO → Entity
    // ==========================
    public Location toEntity() {
        Location location = new Location(city, state);
        location.setId(id);
        return location;
    }
}
