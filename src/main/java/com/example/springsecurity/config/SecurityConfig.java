package com.example.springsecurity.config;

import com.example.springsecurity.data.enumuration.UserRole;
import com.example.springsecurity.filter.CustomAuthenticationEntryPoint;
import com.nimbusds.jose.shaded.gson.internal.LinkedTreeMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint authEntryPoint;

    @Value("${my-app.client.id}")
    String clientId;

    @Value("${keycloak.admin.username}")
    String adminUsername;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("**/login", "**/refresh_token")
                .permitAll()
                .and()
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .oauth2ResourceServer(httpSecurity -> httpSecurity.authenticationEntryPoint(authEntryPoint))
                .csrf()
                .disable()
        ;
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverterForKeycloak() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter = jwt -> {
            List<String> clientRoles = new ArrayList<>();
            String username;
            try {
                username = jwt.getClaim("preferred_username");
            } catch (Exception e) {
                username = "undefined";
            }
            MDC.put("username", username);

            HttpServletRequest httpServletRequest = ((ServletRequestAttributes) Objects.requireNonNull(
                    RequestContextHolder.getRequestAttributes()))
                    .getRequest();
            if (httpServletRequest.getRequestURI().contains("logout")) {
                httpServletRequest.setAttribute("username", username);
            }

            if (username.equals(adminUsername)) {
                clientRoles = Arrays.stream(UserRole.values()).map(UserRole::toString).collect(Collectors.toList());
            } else {
                Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
                Object client = resourceAccess.get(clientId);

                if (client == null) {
                    clientRoles.add(UserRole.NORMAL.toString());
                } else {
                    LinkedTreeMap<String, List<String>> clientRoleMap = (LinkedTreeMap<String, List<String>>) client;
                    List<String> roles = new ArrayList<>(clientRoleMap.get("roles"));
                    clientRoles.addAll(roles);
                }
            }

            return clientRoles
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        };
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}