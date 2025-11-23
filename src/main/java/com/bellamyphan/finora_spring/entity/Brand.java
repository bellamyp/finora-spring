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

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_brands_users"))
    private User user;

    @Column(name = "name", nullable = false, length = 50)
    @NotBlank(message = "Brand name is required")
    private String name;

    @Column(name = "location", length = 50)
    private String location;

    // Constructor without ID (Java can generate NanoID separately)
    public Brand(User user, String name, String location) {
        this.user = user;
        this.name = name;
        this.location = location;
    }
}
