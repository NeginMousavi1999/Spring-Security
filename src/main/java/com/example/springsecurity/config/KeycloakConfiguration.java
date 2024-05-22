package com.example.springsecurity.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfiguration {
    @Value("${my-app.client.id}")
    String clientId;

    @Value("${my-app.client.secret}")
    String clientSecret;

    @Value("${keycloak.realmName}")
    String realmName;

    @Value("${keycloak.url}")
    String url;

    @Bean
    public Keycloak getKeycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(url)
                .realm(realmName)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType("client_credentials")
                .build();
    }
}