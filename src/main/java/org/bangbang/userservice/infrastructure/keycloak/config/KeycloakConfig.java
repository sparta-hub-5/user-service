package org.bangbang.userservice.infrastructure.keycloak.config;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakConfig {
    private final KeycloakProperties properties;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
            .serverUrl(properties.getServerUrl())
            .realm(properties.getRealm())
            .clientId(properties.getClientId())
            .username(properties.getAdminUsername())
            .password(properties.getAdminPassword())
            .build();
    }
}
