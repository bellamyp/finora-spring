package com.bellamyphan.finora_spring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data // Lombok annotation to generate getters, setters, equals, hashCode, and toString methods
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id; // NanoID 10-char

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 60)
    @NotBlank(message = "Email is required")
    private String email;

    @PrePersist
    @PreUpdate
    public void normalizeEmail() {
        setEmail(email); // reuse setter logic
    }

    @Column(name = "password", nullable = false, length = 60)
    @NotBlank(message = "Password is required")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = "fk_users_roles"))
    private Role role;

    // Constructor without id (id can be generated in service layer)
    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Override setter to force lowercase
    public void setEmail(String email) {
        if (email != null) {
            this.email = email.toLowerCase();
        } else {
            this.email = null;
        }
    }
}
