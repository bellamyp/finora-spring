package com.bellamyphan.finora_spring.entity;

import jakarta.persistence.*;
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

    @Column(name = "url")
    private String url;

    // Constructor without ID (Java can generate NanoID separately)
    public Brand(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
