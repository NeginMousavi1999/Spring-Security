package com.example.springsecurity.exception;

import com.example.springsecurity.data.enumuration.ResponseCode;
import lombok.Getter;

@Getter
public class APIException extends RuntimeException {
    private final String message;
    private final int code;
    private final ResponseCode responseCode;
    private final String details;

    public APIException(ResponseCode responseCode) {
        this.message = responseCode.getDescription();
        this.code = responseCode.getCode();
        this.responseCode = responseCode;
        this.details = "notSpecified";
    }

    public APIException(ResponseCode responseCode, String details) {
        this.message = responseCode.getDescription();
        this.code = responseCode.getCode();
        this.responseCode = responseCode;
        this.details = details;
    }
}
