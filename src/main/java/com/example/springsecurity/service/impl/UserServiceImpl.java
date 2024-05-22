package com.example.springsecurity.service.impl;

import com.example.springsecurity.data.dto.request.LoginRequestDTO;
import com.example.springsecurity.data.dto.response.LoginResponseDTO;
import com.example.springsecurity.service.IUserService;
import com.example.springsecurity.ws.KeycloakCaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Negin Mousavi 5/14/2024 - Tuesday
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final KeycloakCaller keycloakCaller;

    @Override
    public LoginResponseDTO login(LoginRequestDTO requestDTO) {
        log.debug("... calling the get token api start ...");
        return keycloakCaller.login(
                requestDTO.getUsername(),
                requestDTO.getPassword()
        );
    }

    @Override
    public LoginResponseDTO refreshToken(String refreshToken) {
        log.debug("... calling the refresh token api start ...");
        List<String> tokens = keycloakCaller.refreshToken(refreshToken);
        return new LoginResponseDTO(
                tokens.get(0),
                tokens.get(1)
        );
    }

    @Override
    public void logout(String username) {
        log.debug("... logout start ...");
        keycloakCaller.logout(username);
    }
}