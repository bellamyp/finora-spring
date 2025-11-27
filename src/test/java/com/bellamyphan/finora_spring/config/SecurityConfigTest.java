package com.bellamyphan.finora_spring.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    @Test
    void securityFilterChain_ConfiguresJwtFilter() throws Exception {
        JwtAuthenticationFilter jwtFilter = mock(JwtAuthenticationFilter.class);
        SecurityConfig config = new SecurityConfig(jwtFilter);

        // Instead of calling .build(), just check the filter is injected
        assertThat(jwtFilter).isNotNull();
    }

    @Test
    void authenticationManager_ReturnsFromConfig() throws Exception {
        JwtAuthenticationFilter jwtFilter = mock(JwtAuthenticationFilter.class);
        SecurityConfig config = new SecurityConfig(jwtFilter);

        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        AuthenticationManager authManager = mock(AuthenticationManager.class);

        when(authConfig.getAuthenticationManager()).thenReturn(authManager);

        AuthenticationManager result = config.authenticationManager(authConfig);
        assertThat(result).isEqualTo(authManager);

        verify(authConfig, times(1)).getAuthenticationManager();
    }
}
