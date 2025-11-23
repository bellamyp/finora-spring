package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.BrandCreateDto;
import com.bellamyphan.finora_spring.dto.BrandDto;
import com.bellamyphan.finora_spring.entity.Brand;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.BrandService;
import com.bellamyphan.finora_spring.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandControllerTest {

    @Mock
    private BrandService brandService;

    @Mock
    private UserService userService;

    @InjectMocks
    private BrandController brandController;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    // -------------------------
    // POST /api/brands
    // -------------------------
    @Test
    void createBrand_shouldReturnBrandDto() {
        // Mock JWT authentication
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user123");

        // Mock UserService
        User user = new User();
        user.setId("user123");
        when(userService.findById("user123")).thenReturn(Optional.of(user));

        // Mock BrandService response
        BrandCreateDto request = new BrandCreateDto("Nike", "Houston");
        BrandDto response = new BrandDto("id123", "Nike", "Houston");

        when(brandService.createBrand(any(BrandCreateDto.class), any(User.class)))
                .thenReturn(response);

        // Act
        ResponseEntity<BrandDto> result = brandController.createBrand(request);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo("id123");
        assertThat(result.getBody().getName()).isEqualTo("Nike");

        verify(brandService).createBrand(request, user);
    }

    // -------------------------
    // GET /api/brands
    // -------------------------
    @Test
    void getBrandsByUser_shouldReturnListOfBrandDto() {
        // Mock JWT authentication
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user123");

        // Mock User
        User user1 = new User();
        user1.setId("user123");
        when(userService.findById("user123")).thenReturn(Optional.of(user1));

        when(brandService.findBrandsByUser(user1)).thenReturn(List.of(
                new Brand(user1, "Apple", "Cupertino"),
                new Brand(user1, "Samsung", "Seoul")
        ));

        // Instead of using Brand entity in controller conversion, you can mock BrandDto mapping if needed
        // Here we directly check that controller maps Brand → BrandDto correctly
        List<BrandDto> result = brandController.getBrandsByUser();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Apple");
        assertThat(result.get(0).getLocation()).isEqualTo("Cupertino");

        assertThat(result.get(1).getName()).isEqualTo("Samsung");
        assertThat(result.get(1).getLocation()).isEqualTo("Seoul");

        verify(userService).findById("user123");
        verify(brandService).findBrandsByUser(user1);
    }
}
