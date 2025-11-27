package com.bellamyphan.finora_spring.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NanoIdServiceTest {

    private NanoIdService nanoIdService;

    private final int defaultLength = 10;

    @Mock
    private JpaRepository<Object, String> repository;

    @BeforeEach
    void setUp() {
        nanoIdService = new NanoIdService(defaultLength);
    }

    // ---------------------------------------------
    // 1️⃣ Success test — ID is returned when unused
    // ---------------------------------------------
    @Test
    void generateUniqueId_shouldReturnId_WhenNotExists() {
        try (MockedStatic<NanoIdUtils> utilities = mockStatic(NanoIdUtils.class)) {

            utilities.when(() ->
                            NanoIdUtils.randomNanoId(any(), any(), eq(defaultLength)))
                    .thenReturn("ID_123456789");

            when(repository.existsById("ID_123456789")).thenReturn(false);

            String result = nanoIdService.generateUniqueId(repository);

            assertEquals("ID_123456789", result);
        }
    }

    // -----------------------------------------------------
    // 2️⃣ Retry logic — first ID collides, second succeeds
    // -----------------------------------------------------
    @Test
    void generateUniqueId_shouldRetryOnCollision() {
        try (MockedStatic<NanoIdUtils> utilities = mockStatic(NanoIdUtils.class)) {

            utilities.when(() ->
                            NanoIdUtils.randomNanoId(any(), any(), eq(defaultLength)))
                    .thenReturn("DUPLICATE", "UNIQUE");

            // Simulate collision then success
            when(repository.existsById("DUPLICATE")).thenReturn(true);
            when(repository.existsById("UNIQUE")).thenReturn(false);

            String result = nanoIdService.generateUniqueId(repository);

            assertEquals("UNIQUE", result);

            verify(repository).existsById("DUPLICATE");
            verify(repository).existsById("UNIQUE");
        }
    }

    // ---------------------------------------------------
    // 3️⃣ Fail after 10 attempts — all returned IDs exist
    // ---------------------------------------------------
    @Test
    void generateUniqueId_shouldFailAfter10Attempts() {
        try (MockedStatic<NanoIdUtils> utilities = mockStatic(NanoIdUtils.class)) {

            utilities.when(() ->
                            NanoIdUtils.randomNanoId(any(), any(), eq(defaultLength)))
                    .thenReturn("X"); // always returns same collision

            when(repository.existsById("X")).thenReturn(true);

            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> nanoIdService.generateUniqueId(repository)
            );

            assertTrue(ex.getMessage().contains("10 attempts"));

            verify(repository, times(10)).existsById("X");
        }
    }

    // ---------------------------------------------------------
    // 4️⃣ Real random string length test (sanity check)
    // ---------------------------------------------------------
    @Test
    void generateUniqueId_realNanoIdLength() {
        when(repository.existsById(anyString())).thenReturn(false);

        String id = nanoIdService.generateUniqueId(repository);

        assertNotNull(id);
        assertEquals(defaultLength, id.length());
    }
}
