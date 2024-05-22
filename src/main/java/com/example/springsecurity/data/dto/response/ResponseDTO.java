package com.example.springsecurity.data.dto.response;

import com.example.springsecurity.data.enumuration.ResponseCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ResponseDTO<T> {
    @Schema(title = "response code")
    int responseCode = ResponseCode.OK_OPERATION.getCode();

    @Schema(title = "response message")
    String responseMessage = ResponseCode.OK_OPERATION.getDescription();

    @Schema(title = "response information")
    T responseData;

    String responseDateTime = LocalDateTime.now().toString();

    HttpStatus httpStatus;

    @Override
    public String toString() {
        return "{" +
                "resCode=" + responseCode +
                '}';
    }
}