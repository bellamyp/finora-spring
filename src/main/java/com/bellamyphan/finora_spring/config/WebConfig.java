package com.bellamyphan.finora_spring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    String[] allowedOrigins = {
            "http://localhost:4200",
            "https://finora-angular.vercel.app"
    };

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Define the allowed origins for CORS
        logger.info("Configuring CORS settings for /api/** and specific login/logout endpoints");
        // Allow cross-origin requests for all endpoints under /api/**
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
        logger.debug("CORS configuration for /api/** set with allowed origins: {}", (Object) allowedOrigins);
//        // Allow cross-origin requests for /login endpoint
//        registry.addMapping("/login")
//                .allowedOrigins(allowedOrigins)
//                .allowedMethods("POST")
//                .allowedHeaders("*")
//                .allowCredentials(true);
//        logger.debug("CORS configuration for /login set with allowed origins: {}", (Object) allowedOrigins);
//        // Allow cross-origin requests for /logout endpoint
//        registry.addMapping("/logout")
//                .allowedOrigins(allowedOrigins)
//                .allowedMethods("POST")
//                .allowedHeaders("*")
//                .allowCredentials(true);
        logger.debug("CORS configuration for /logout set with allowed origins: {}", (Object) allowedOrigins);
    }
}
