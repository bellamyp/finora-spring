package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.BrandCreateDto;
import com.bellamyphan.finora_spring.dto.BrandDto;
import com.bellamyphan.finora_spring.service.BrandService;
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
import static org.mockito.Mockito.*;

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
        BrandCreateDto request = new BrandCreateDto("Nike", "https://nike.com");
        BrandDto response = new BrandDto("id123", "Nike", "https://nike.com");

        when(brandService.createBrand(any(BrandCreateDto.class)))
                .thenReturn(response);

        ResponseEntity<BrandDto> result = brandController.createBrand(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo("id123");
        assertThat(result.getBody().getName()).isEqualTo("Nike");

        verify(brandService).createBrand(request);
    }

    // -------------------------
    // GET /api/brands
    // -------------------------
    @Test
    void getBrands_shouldReturnListOfBrandDto() {
        List<BrandDto> mockBrands = List.of(
                new BrandDto("id1", "Apple", "https://apple.com"),
                new BrandDto("id2", "Samsung", "https://samsung.com")
        );

        when(brandService.getAllBrands()).thenReturn(mockBrands);

        ResponseEntity<List<BrandDto>> response = brandController.getBrands();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);

        assertThat(response.getBody().get(0).getName()).isEqualTo("Apple");
        assertThat(response.getBody().get(1).getName()).isEqualTo("Samsung");

        verify(brandService).getAllBrands();
    }
}
