package com.example.springsecurity.controller;

import com.example.springsecurity.config.MessageSourceConfiguration;
import com.example.springsecurity.data.dto.response.ResponseDTO;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@FieldDefaults(level = AccessLevel.PROTECTED)
public class BaseController {
    @Autowired
    private MessageSourceConfiguration messageSource;

    protected <T> ResponseEntity<T> generateResponse(T body) {
        return ResponseEntity.ok()
                .header("Expires", "0")
                .header("Cache-Control", "must-revalidate, post-check=0, pre-check=0")
                .header("Pragma", "public")
                .body(body);
    }

    protected <T> ResponseEntity<ResponseDTO<T>> generateResponse(ResponseDTO<T> body, String lan) {
        body.setResponseMessage(
                messageSource.getMessage(
                        body.getResponseMessage(),
                        lan
                )
        );
        body.setHttpStatus(HttpStatus.OK);
        return ResponseEntity.ok()
                .header("Expires", "0")
                .header("Cache-Control", "must-revalidate, post-check=0, pre-check=0")
                .header("Pragma", "public")
                .body(body);
    }

    protected String getToken(String token) {
        return token.replace("Bearer ", "");
    }

    protected String getMessageSource(String key, String lan) {
        return messageSource.getMessage(
                key,
                lan
        );
    }

    protected String getMessageSourceEn(String key) {
        return messageSource.getMessage(key, "en");
    }
}