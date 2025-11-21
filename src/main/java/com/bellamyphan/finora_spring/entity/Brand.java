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
@Table(name = "brands")
@NoArgsConstructor
public class Brand {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id; // NanoID 10-char, generated in Java

    @Column(name = "name", nullable = false, length = 50)
    @NotBlank(message = "Brand name is required")
    private String name;

    @Column(name = "location", length = 50)
    private String location;

    // Constructor with all fields
    public Brand(String id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    // Constructor without ID (Java can generate NanoID separately)
    public Brand(String name, String location) {
        this.name = name;
        this.location = location;
    }
}
