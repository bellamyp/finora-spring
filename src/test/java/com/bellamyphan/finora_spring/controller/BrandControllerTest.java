package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.BrandCreateDto;
import com.bellamyphan.finora_spring.dto.BrandDto;
import com.bellamyphan.finora_spring.service.BrandService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrandControllerTest {

    @Mock
    private BrandService brandService;

    @InjectMocks
    private BrandController brandController;

    // -------------------------
    // POST /api/brands
    // -------------------------
    @Test
    void createBrand_shouldReturnBrandDto() {
        BrandCreateDto request = new BrandCreateDto("Nike", "Houston");
        BrandDto response = new BrandDto("id123", "Nike", "Houston");

        when(brandService.createBrand(any(BrandCreateDto.class))).thenReturn(response);

        ResponseEntity<BrandDto> result = brandController.createBrand(request);

        // Use getStatusCode() with HttpStatus enum
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo("id123");
        assertThat(result.getBody().getName()).isEqualTo("Nike");
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

        // Updated status check
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(result.getBody()).isInstanceOf(List.class);
        Assertions.assertNotNull(result.getBody());
        assertThat(((List<?>) result.getBody()).size()).isEqualTo(2);
    }

    @Test
    void searchBrandByName_emptyName_shouldReturnBadRequest() {
        ResponseEntity<?> result = brandController.searchBrandByName("");

        // Updated status check
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(result.getBody()).isEqualTo("Search name cannot be empty.");
    }
}
