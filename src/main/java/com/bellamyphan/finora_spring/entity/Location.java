package com.bellamyphan.finora_spring.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "locations")
@NoArgsConstructor
public class Location {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id; // NanoID 10-char, generated in Java

    @Column(name = "city", nullable = false, length = 50)
    @NotBlank(message = "City name is required")
    private String city;

    @Column(name = "state", nullable = false, length = 50)
    @NotBlank(message = "State name is required")
    private String state;

    // Constructor without ID (Java can generate NanoID separately)
    public Location(String city, String state) {
        this.city = city;
        this.state = state;
    }
}
