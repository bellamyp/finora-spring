package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.dto.BrandCreateDto;
import com.bellamyphan.finora_spring.dto.BrandDto;
import com.bellamyphan.finora_spring.entity.Brand;
import com.bellamyphan.finora_spring.repository.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private NanoIdService nanoIdService;

    @InjectMocks
    private BrandService brandService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------------
    // Test: searchByName
    // -------------------------
    @Test
    void searchByName_shouldReturnDtos() {
        Brand brand1 = new Brand("Nike", "Houston");
        Brand brand2 = new Brand("Nine West", "Dallas");

        when(brandRepository.findByNameContainingIgnoreCase("ni"))
                .thenReturn(List.of(brand1, brand2));

        List<BrandDto> result = brandService.searchByName("ni");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Nike");
        assertThat(result.get(1).getLocation()).isEqualTo("Dallas");
    }

    // -------------------------
    // Test: createBrand success
    // -------------------------
    @Test
    void createBrand_shouldCreateSuccessfully() {
        BrandCreateDto request = new BrandCreateDto("Nike", "Houston");
        Brand savedBrand = new Brand("Nike", "Houston");

        when(brandRepository.existsByNameIgnoreCaseAndLocationIgnoreCase("Nike", "Houston"))
                .thenReturn(false);
        when(nanoIdService.generate()).thenReturn("id123");
        when(brandRepository.save(any(Brand.class))).thenReturn(savedBrand);

        BrandDto result = brandService.createBrand(request);

        assertThat(result.getName()).isEqualTo("Nike");
        assertThat(result.getLocation()).isEqualTo("Houston");

        verify(brandRepository).save(any(Brand.class));
    }

    // -------------------------
    // Test: createBrand duplicate -> exception
    // -------------------------
    @Test
    void createBrand_duplicate_shouldThrow() {
        BrandCreateDto request = new BrandCreateDto("Nike", "Houston");

        when(brandRepository.existsByNameIgnoreCaseAndLocationIgnoreCase("Nike", "Houston"))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> brandService.createBrand(request)
        );

        assertThat(exception.getMessage())
                .contains("Brand 'Nike' at location 'Houston' already exists");
    }

    // -------------------------
    // Test: createBrand collision retry -> exception
    // -------------------------
    @Test
    void createBrand_collisionRetry_shouldThrow() {
        BrandCreateDto request = new BrandCreateDto("Nike", "Houston");

        when(brandRepository.existsByNameIgnoreCaseAndLocationIgnoreCase("Nike", "Houston"))
                .thenReturn(false);
        when(nanoIdService.generate()).thenReturn("id123");
        when(brandRepository.save(any(Brand.class))).thenThrow(DataIntegrityViolationException.class);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> brandService.createBrand(request)
        );

        assertThat(exception.getMessage())
                .contains("Failed to generate unique Brand ID after 10 attempts");

        verify(brandRepository, times(10)).save(any(Brand.class));
    }
}
