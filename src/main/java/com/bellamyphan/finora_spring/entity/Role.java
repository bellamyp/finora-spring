package com.bellamyphan.finora_spring.entity;

import com.bellamyphan.finora_spring.constant.RoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data // Lombok will generate getters, setters, toString, equals, and hashCode methods
@NoArgsConstructor
public class Role {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id; // NanoID 10-char

    @Enumerated(EnumType.STRING) // Store enum as string in DB
    @Column(name = "name", nullable = false, unique = true, length = 50)
    @NotNull(message = "Role is required")
    private RoleEnum name;

    // Constructor with only enum (id can be generated separately)
    public Role(RoleEnum name) {
        this.name = name;
    }
}