package com.example.springsecurity.exception;

import com.example.springsecurity.data.enumuration.ResponseCode;
import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {
    private final ResponseCode responseCode;

    public UnauthorizedException() {
        this.responseCode = ResponseCode.TOKEN_NOT_VALID;
    }
}
