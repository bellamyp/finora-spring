package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.BrandCreateDto;
import com.bellamyphan.finora_spring.dto.BrandDto;
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

        ResponseEntity<BrandDto> result = brandController.createBrand(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo("id123");
        assertThat(result.getBody().getName()).isEqualTo("Nike");

        verify(brandService).createBrand(request, user);
    }

    // -------------------------
    // GET /api/brands/search
    // -------------------------
    @Test
    void searchBrandByName_shouldReturnList() {
        List<BrandDto> brands = List.of(
                new BrandDto("id1", "Nike", "Houston"),
                new BrandDto("id2", "Nine West", "Dallas")
        );

        when(brandService.searchByName("ni")).thenReturn(brands);

        ResponseEntity<?> result = brandController.searchBrandByName("ni");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isInstanceOf(List.class);
        assertThat(((List<?>) result.getBody()).size()).isEqualTo(2);
    }

    @Test
    void searchBrandByName_emptyName_shouldReturnBadRequest() {
        ResponseEntity<?> result = brandController.searchBrandByName("");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isEqualTo("Search name cannot be empty.");
    }
}
