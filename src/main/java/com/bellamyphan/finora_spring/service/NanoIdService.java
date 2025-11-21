package com.bellamyphan.finora_spring.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NanoIdService {

    private final int defaultLength;

    // Inject default length from application.properties
    public NanoIdService(@Value("${nanoid.default.length:10}") int defaultLength) {
        this.defaultLength = defaultLength;
    }

    /**
     * Generates a random NanoID with the default length (from application.properties)
     *
     * @return random NanoID string
     */
    public String generate() {
        return NanoIdUtils.randomNanoId(
                NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                NanoIdUtils.DEFAULT_ALPHABET,
                defaultLength
        );
    }
}
