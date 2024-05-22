package com.example.springsecurity.service.impl;

import com.example.springsecurity.data.dto.request.LoginRequestDTO;
import com.example.springsecurity.data.dto.response.LoginResponseDTO;
import com.example.springsecurity.service.IUserService;
import com.example.springsecurity.ws.KeycloakCaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
