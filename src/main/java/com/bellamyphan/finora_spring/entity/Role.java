package com.bellamyphan.finora_spring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "roles")
@Data // Lombok will generate getters, setters, toString, equals, and hashCode methods
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID DEFAULT uuid_generate_v4()")
    private UUID id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Role name is required")
    private String name;

    // Constructor with parameters excluding id
    public Role(String name) {
        this.name = name;
    }
}