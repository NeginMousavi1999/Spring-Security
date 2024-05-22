package com.example.springsecurity.service;

import com.example.springsecurity.data.dto.request.LoginRequestDTO;
import com.example.springsecurity.data.dto.response.LoginResponseDTO;

/**
 * @author Negin Mousavi 5/14/2024 - Tuesday
 */
public interface IUserService {
    LoginResponseDTO login(LoginRequestDTO requestDTO);
}
