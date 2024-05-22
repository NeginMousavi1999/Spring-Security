package com.example.springsecurity.filter;

import com.example.springsecurity.config.MessageSourceConfiguration;
import com.example.springsecurity.data.dto.response.ResponseDTO;
import com.example.springsecurity.data.enumuration.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final ObjectWriter OBJECT_WRITER = new ObjectMapper().writer().withDefaultPrettyPrinter();
    private static final String CHARSET = "application/json; charset=utf-8";
    private final MessageSourceConfiguration messageSource;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ResponseCode responseCode = ResponseCode.TOKEN_NOT_VALID;
        int code = responseCode.getCode();
        HttpStatus httpStatusCode = HttpStatus.UNAUTHORIZED;
        ResponseDTO<Object> responseDTO = new ResponseDTO<>();
        responseDTO.setResponseCode(code);
        responseDTO.setResponseMessage(messageSource.getMessage(responseCode.getDescription()));
        responseDTO.setHttpStatus(httpStatusCode);
        log.error(
                "... [{}] , [{}] , [{}] ...",
                code,
                messageSource.getMessage(
                        responseCode.getDescription(),
                        request.getHeader("accept-language")
                ),
                authException.getLocalizedMessage()
        );
        String json = OBJECT_WRITER.writeValueAsString(responseDTO);
        response.setStatus(httpStatusCode.value());
        response.setHeader(HttpHeaders.CONTENT_TYPE, CHARSET);
        response.getWriter().write(json);
    }
}
