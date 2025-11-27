package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.dto.BrandCreateDto;
import com.bellamyphan.finora_spring.dto.BrandDto;
import com.bellamyphan.finora_spring.entity.Brand;
import com.bellamyphan.finora_spring.repository.BrandRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private NanoIdService nanoIdService;

    @InjectMocks
    private BrandService brandService;

    // ------------------------------------------------------------
    // SUCCESS: createBrand
    // ------------------------------------------------------------
    @Test
    void createBrand_shouldCreateSuccessfully() {
        BrandCreateDto request = new BrandCreateDto(" Nike ", " HTTP://example.com/Brand ");

        // Normalize results
        String normalizedName = "Nike";
        String normalizedUrl = "http://example.com/brand";

        when(brandRepository.existsByNameIgnoreCase(normalizedName)).thenReturn(false);
        when(brandRepository.existsByUrlIgnoreCase(normalizedUrl)).thenReturn(false);

        when(nanoIdService.generateUniqueId(brandRepository)).thenReturn("id123");

        Brand saved = new Brand(normalizedName, normalizedUrl);
        saved.setId("id123");
        when(brandRepository.save(any(Brand.class))).thenReturn(saved);

        BrandDto result = brandService.createBrand(request);

        assertThat(result.getId()).isEqualTo("id123");
        assertThat(result.getName()).isEqualTo(normalizedName);
        assertThat(result.getUrl()).isEqualTo(normalizedUrl);

        // capture Brand being saved
        ArgumentCaptor<Brand> brandCaptor = ArgumentCaptor.forClass(Brand.class);
        verify(brandRepository).save(brandCaptor.capture());

        Brand captured = brandCaptor.getValue();
        assertThat(captured.getName()).isEqualTo(normalizedName);
        assertThat(captured.getUrl()).isEqualTo(normalizedUrl);
    }

    // ------------------------------------------------------------
    // FAIL: duplicate name
    // ------------------------------------------------------------
    @Test
    void createBrand_duplicateName_shouldThrow() {
        BrandCreateDto request = new BrandCreateDto("Nike", "https://example.com");

        when(brandRepository.existsByNameIgnoreCase("Nike"))
                .thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> brandService.createBrand(request)
        );

        assertThat(ex.getMessage()).contains("Brand name already exists");
        verify(brandRepository, never()).save(any());
    }

    // ------------------------------------------------------------
    // FAIL: duplicate URL
    // ------------------------------------------------------------
    @Test
    void createBrand_duplicateUrl_shouldThrow() {
        BrandCreateDto request = new BrandCreateDto("Nike", "https://example.com");

        when(brandRepository.existsByNameIgnoreCase("Nike")).thenReturn(false);
        when(brandRepository.existsByUrlIgnoreCase("https://example.com"))
                .thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> brandService.createBrand(request)
        );

        assertThat(ex.getMessage()).contains("Brand URL already exists");
        verify(brandRepository, never()).save(any());
    }
}
