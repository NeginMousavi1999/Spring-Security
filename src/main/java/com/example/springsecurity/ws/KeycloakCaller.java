package com.example.springsecurity.ws;

import com.example.springsecurity.data.dto.response.LoginResponseDTO;
import com.example.springsecurity.data.enumuration.ResponseCode;
import com.example.springsecurity.data.enumuration.UserRole;
import com.example.springsecurity.exception.APIException;
import com.example.springsecurity.util.JsonUtil;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;

/**
 * @author Negin Mousavi 5/18/2024 - Saturday
 */
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class KeycloakCaller {
    static final String PASSWORD_STRING = "password";
    static final String REFRESH_TOKEN_AS_STRING = "refresh_token";

    @Value("${my-app.client.id}")
    String clientId;

    @Value("${my-app.client.secret}")
    String clientSecret;

    @Value("${keycloak.token-url}")
    String tokenUrl;

    @Value("${keycloak.admin.username}")
    @Getter
    String adminUsername;

    @Value("${keycloak.admin.password}")
    @Getter
    String adminPassword;

    @Getter
    @Setter
    String adminToken;

    private String getExceptionDetails(String action, String e) {
        return "exception happened in calling the keycloak for '" + action + "' and exception is: " + e;
    }

    private List<String> getToken(MultiValueMap<String, String> bodyData) {
        try {
            HttpClient httpClient = HttpClient.create();
            ResponseEntity<String> response = WebClient.builder()
                    .baseUrl(tokenUrl)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build()
                    .post()
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromFormData(bodyData))
                    .retrieve()
                    .toEntity(String.class)
                    .block();
            log.debug("... successfully get token ...");
            return JsonUtil.getValuesFromJsonBody(
                    Objects.requireNonNull(response).getBody(),
                    "access_token",
                    REFRESH_TOKEN_AS_STRING

            );
        } catch (Exception e) {
            if (e.getLocalizedMessage().contains("400")) {
                throw new APIException(
                        ResponseCode.INCORRECT_TOKEN
                );
            } else if (e.getLocalizedMessage().contains("401")) {
                throw new APIException(
                        ResponseCode.INCORRECT_USERNAME_OR_PASSWORD
                );
            } else {
                throw new APIException(
                        ResponseCode.INTERNAL_CALLING_KEYCLOAK_SERVER,
                        getExceptionDetails(tokenUrl, e.getLocalizedMessage())
                );
            }
        }
    }

    public LoginResponseDTO login(String username, String password) {
        List<String> tokens = getToken(username, password);
        String accessToken = tokens.get(0);
        if (username.equals(adminUsername)) {
            return LoginResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(tokens.get(1))
                    .role(UserRole.ADMIN.toString())
                    .build();
        } else {
            try {
                JWTClaimsSet jwtClaimsSet = JWTParser
                        .parse(accessToken)
                        .getJWTClaimsSet();
                log.debug("... jwt claim set : {}", jwtClaimsSet);
            } catch (ParseException e) {
                throw new APIException(ResponseCode.INTERNAL_APP_ERROR, e.getLocalizedMessage());
            }
            return LoginResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(tokens.get(1))
                    .role(UserRole.NORMAL.toString())
                    .build();
        }
    }

    public List<String> getToken(String username, String password) {
        MultiValueMap<String, String> bodyData = new LinkedMultiValueMap<>();
        bodyData.add("client_id", clientId);
        bodyData.add("client_secret", clientSecret);
        bodyData.add("grant_type", PASSWORD_STRING);
        bodyData.add("username", username);
        bodyData.add(PASSWORD_STRING, password);
        return getToken(bodyData);
    }
}
