package com.bellamyphan.finora_spring.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class NanoIdService {

    private final int defaultLength;

    // Inject default length from application.properties
    public NanoIdService(@Value("${nanoid.default.length:10}") int defaultLength) {
        this.defaultLength = defaultLength;
    }

    /**
     * Generates a unique NanoID for a given repository.
     * Automatically retries if there is a collision.
     *
     * @param repository JpaRepository for the entity type
     * @param <T>        entity type
     * @return unique NanoID string
     */
    public <T> String generateUniqueId(JpaRepository<T, String> repository) {
        for (int i = 0; i < 10; i++) {
            String id = generateNanoId();
            if (!repository.existsById(id)) {
                return id;
            }
        }
        throw new RuntimeException("Failed to generate unique NanoID after 10 attempts");
    }

    /**
     * Private NanoID generator — services should use generateUniqueId() instead
     */
    private String generateNanoId() {
        return NanoIdUtils.randomNanoId(
                NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                NanoIdUtils.DEFAULT_ALPHABET,
                defaultLength
        );
    }
}
