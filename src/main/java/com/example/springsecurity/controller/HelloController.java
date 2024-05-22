package com.example.springsecurity.controller;

import com.example.springsecurity.data.dto.response.HelloResponseDTO;
import com.example.springsecurity.data.dto.response.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Validated
public class HelloController extends BaseController {

    @PreAuthorize("hasAnyAuthority(T(com.example.springsecurity.data.enumuration.UserRole).ADMIN)")
    @GetMapping(value = "/admin/hello", produces = "application/json; charset=utf-8")
    public ResponseEntity<ResponseDTO<HelloResponseDTO>> helloAdmin(
            @RequestHeader("Authorization") String token,
            @RequestHeader(value = "accept-language", required = false, defaultValue = "en") String accLang) {
        ResponseDTO<HelloResponseDTO> response = new ResponseDTO<>();
        response.setResponseData(
                new HelloResponseDTO(
                        "hello :) : admin"
                )
        );
        return generateResponse(
                response,
                accLang
        );
    }

    @PreAuthorize("hasAnyAuthority(T(com.example.springsecurity.data.enumuration.UserRole).NORMAL)")
    @GetMapping(value = "/hello", produces = "application/json; charset=utf-8")
    public ResponseEntity<ResponseDTO<HelloResponseDTO>> hello(
            @RequestHeader("Authorization") String token,
            @RequestHeader(value = "accept-language", required = false, defaultValue = "en") String accLang) {
        ResponseDTO<HelloResponseDTO> response = new ResponseDTO<>();
        response.setResponseData(
                new HelloResponseDTO(
                        "hello :) : " + super.getToken(token)
                )
        );
        return generateResponse(
                response,
                accLang
        );
    }
}