package com.example.springsecurity.controller;

import com.example.springsecurity.data.dto.request.LoginRequestDTO;
import com.example.springsecurity.data.dto.response.LoginResponseDTO;
import com.example.springsecurity.data.dto.response.ResponseDTO;
import com.example.springsecurity.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Validated
public class UserController extends BaseController {
    private final IUserService userService;

    @PostMapping(value = "/login", produces = "application/json; charset=utf-8")
    public ResponseEntity<ResponseDTO<LoginResponseDTO>> login(@RequestBody LoginRequestDTO requestDTO) {
        ResponseDTO<LoginResponseDTO> response = new ResponseDTO<>();
        LoginResponseDTO responseDTO = userService.login(requestDTO);
        response.setResponseData(responseDTO);
        return generateResponse(response);
    }
}