package com.example.springsecurity.controller;

import com.example.springsecurity.data.dto.response.ResponseDTO;
import com.example.springsecurity.data.enumuration.ResponseCode;
import com.example.springsecurity.exception.APIException;
import com.example.springsecurity.exception.UnauthorizedException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import io.netty.channel.ConnectTimeoutException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@ControllerAdvice
@Slf4j
@FieldDefaults(level = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class ControllerAdvisor extends BaseController {

    private static final String CHARSET = "application/json; charset=utf-8";
    private static final String LOG_PATTERN = "... [{}] , [{}] ...";
    private static final String CODE_STRING = "code:";

    @ExceptionHandler(BindException.class)
    ResponseEntity<ResponseDTO<Object>> handleException(BindException exception,
                                                        final HttpServletRequest request) {
        ResponseDTO<Object> responseDTO = new ResponseDTO<>();
        for (FieldError fieldError : exception.getFieldErrors()) {
            String message = fieldError.getDefaultMessage();
            assert message != null;
            if (message.contains(CODE_STRING)) {
                int code = Integer.parseInt(message.replace(CODE_STRING, ""));
                responseDTO.setResponseCode(code);
                responseDTO.setResponseMessage(
                        getMessageSource(
                                Objects.requireNonNull(ResponseCode.getValue(code))
                                        .getDescription(),
                                request.getHeader("accept-language")
                        )
                );
                log.error(
                        "...  [{}] , [{} : {} : {}] ...",
                        code,
                        fieldError.getField(),
                        fieldError.getCode(),
                        fieldError.getRejectedValue()
                );
            } else {
                log(
                        fieldError.getRejectedValue(),
                        exception.getLocalizedMessage()
                );
                responseDTO.setResponseMessage(message);
            }
        }
        return ResponseEntity.badRequest().header(HttpHeaders.CONTENT_TYPE, CHARSET).body(responseDTO);
    }

    @ExceptionHandler(APIException.class)
    ResponseEntity<ResponseDTO<Object>> handleException(APIException exception,
                                                        final HttpServletRequest request) {
        int code = exception.getCode();
        ResponseDTO<Object> responseDTO = getResponseDTO(
                code,
                exception.getMessage(),
                HttpStatus.BAD_REQUEST,
                getLangHeader(request)
        );
        log(
                code,
                exception.getDetails()
        );
        return ResponseEntity.badRequest().header(HttpHeaders.CONTENT_TYPE, CHARSET).body(responseDTO);
    }

    @ExceptionHandler(UnauthorizedException.class)
    ResponseEntity<ResponseDTO<Object>> handleException(UnauthorizedException exception,
                                                        final HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        ResponseCode responseCode = exception.getResponseCode();
        int code = responseCode.getCode();
        ResponseDTO<Object> responseDTO = getResponseDTO(
                code,
                responseCode.getDescription(),
                httpStatus,
                getLangHeader(request)
        );
        log(
                code,
                getMessageSourceEn(responseCode.getDescription())
        );
        return ResponseEntity
                .status(httpStatus)
                .header(HttpHeaders.CONTENT_TYPE, CHARSET)
                .body(responseDTO);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ResponseDTO<Object>> handleException(AccessDeniedException exception,
                                                        final HttpServletRequest request) {
        return responseEntity(
                exception.getMessage() +
                        " for " +
                        request.getRequestURI() +
                        " and roles are: " +
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getAuthorities()
                                .toString(),
                ResponseCode.ACCESS_DENIED,
                HttpStatus.FORBIDDEN,
                getLangHeader(request)
        );
    }

    @ExceptionHandler(ConnectTimeoutException.class)
    ResponseEntity<ResponseDTO<Object>> handleException(ConnectTimeoutException exception,
                                                        final HttpServletRequest request) {
        return internalServerError(
                exception.getMessage(),
                ResponseCode.INTERNAL_CONNECTION_TIMEOUT,
                getLangHeader(request)
        );
    }

    @ExceptionHandler(UnexpectedRollbackException.class)
    ResponseEntity<ResponseDTO<Object>> handleException(UnexpectedRollbackException exception,
                                                        final HttpServletRequest request) {
        return internalServerError(
                exception.getLocalizedMessage(),
                ResponseCode.INTERNAL_APP_ERROR,
                getLangHeader(request)
        );
    }

    @ExceptionHandler(NullPointerException.class)
    ResponseEntity<ResponseDTO<Object>> handleException(NullPointerException exception,
                                                        final HttpServletRequest request) {
        return internalServerError(
                getInternalExcMessage(exception),
                ResponseCode.INTERNAL_APP_ERROR,
                getLangHeader(request)
        );
    }

    @ExceptionHandler(StringIndexOutOfBoundsException.class)
    ResponseEntity<ResponseDTO<Object>> handleException(StringIndexOutOfBoundsException exception,
                                                        final HttpServletRequest request) {
        return internalServerError(
                getInternalExcMessage(exception),
                ResponseCode.INTERNAL_APP_ERROR,
                getLangHeader(request)
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ResponseDTO<Object>> handleException(IllegalArgumentException exception,
                                                        final HttpServletRequest request) {
        return internalServerError(
                getInternalExcMessage(exception),
                ResponseCode.INTERNAL_APP_ERROR,
                getLangHeader(request)
        );
    }

    // input not mapping to target definition
    @ExceptionHandler(InvalidTypeIdException.class)
    ResponseEntity<ResponseDTO<Object>> handleException(InvalidTypeIdException exception,
                                                        final HttpServletRequest request) {
        return badRequest(
                exception.getMessage(),
                ResponseCode.INTERNAL_INPUT_MISMATCHED_JSON_VALUE,
                getLangHeader(request)
        );
    }

    // wrong mapping HTTP request, for example method is GET but request is POST
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ResponseDTO<Object>> handleException(HttpRequestMethodNotSupportedException exception,
                                                        final HttpServletRequest request) {
        return badRequest(
                exception.getLocalizedMessage(),
                ResponseCode.METHOD_NOT_ALLOWED,
                getLangHeader(request)
        );
    }

    // if media is not json, for example it is xml or text
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<ResponseDTO<Object>> handleException(HttpMediaTypeNotSupportedException exception,
                                                        final HttpServletRequest request) {
        return badRequest(
                exception.getLocalizedMessage(),
                ResponseCode.INVALID_CONTENT_TYPE,
                getLangHeader(request)
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    ResponseEntity<ResponseDTO<Object>> handleException(NoHandlerFoundException exception,
                                                        final HttpServletRequest request) {
        return badRequest(
                exception.getLocalizedMessage(),
                ResponseCode.INVALID_PATH,
                getLangHeader(request)
        );
    }

    // miss RequestAttribute
    @ExceptionHandler(ServletRequestBindingException.class)
    ResponseEntity<ResponseDTO<Object>> handleException(ServletRequestBindingException exception,
                                                        final HttpServletRequest request) {
        String exceptionMessage = exception.getLocalizedMessage();
        String name = ((MissingRequestHeaderException) exception).getHeaderName();
        if (name.equals("Authorization")) {
            return responseEntity(
                    exceptionMessage,
                    ResponseCode.TOKEN_NOT_VALID,
                    HttpStatus.UNAUTHORIZED,
                    getLangHeader(request)
            );
        }
        return badRequest(
                exceptionMessage,
                ResponseCode.MISS_REQUEST_PARAM,
                getLangHeader(request)
        );
    }

    @ExceptionHandler(ClassCastException.class)
    ResponseEntity<ResponseDTO<Object>> handleException(ClassCastException exception,
                                                        final HttpServletRequest request) {
        return internalServerError(
                getInternalExcMessage(exception),
                ResponseCode.INTERNAL_APP_ERROR,
                getLangHeader(request)
        );
    }

    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)
    ResponseEntity<ResponseDTO<Object>> handleException(ArrayIndexOutOfBoundsException exception,
                                                        final HttpServletRequest request) {
        return internalServerError(
                getInternalExcMessage(exception),
                ResponseCode.INTERNAL_APP_ERROR,
                getLangHeader(request)
        );
    }

    private ResponseEntity<ResponseDTO<Object>> internalServerError(String exceptionMessage,
                                                                    ResponseCode responseCode,
                                                                    String lan) {
        return responseEntity(
                exceptionMessage,
                responseCode,
                HttpStatus.INTERNAL_SERVER_ERROR,
                lan
        );
    }

    private ResponseEntity<ResponseDTO<Object>> badRequest(String exceptionMessage,
                                                           ResponseCode responseCode,
                                                           String lan) {
        return responseEntity(
                exceptionMessage,
                responseCode,
                HttpStatus.BAD_REQUEST,
                lan
        );
    }

    private ResponseEntity<ResponseDTO<Object>> responseEntity(String exceptionMessage,
                                                               ResponseCode responseCode,
                                                               HttpStatus responseStatus,
                                                               String lan) {
        int code = responseCode.getCode();
        ResponseDTO<Object> responseDTO = getResponseDTO(
                code,
                responseCode.getDescription(),
                responseStatus,
                lan
        );
        log(code, exceptionMessage);
        return ResponseEntity.status(responseStatus).header(HttpHeaders.CONTENT_TYPE, CHARSET).body(responseDTO);
    }

    private ResponseDTO<Object> getResponseDTO(int code, String responseCode, HttpStatus httpStatus, String lan) {
        ResponseDTO<Object> responseDTO = new ResponseDTO<>();
        responseDTO.setResponseCode(code);
        responseDTO.setResponseMessage(
                getMessageSource(
                        responseCode,
                        lan
                )
        );
        responseDTO.setHttpStatus(httpStatus);
        return responseDTO;
    }

    private String getInternalExcMessage(Exception exception) {
        String exceptionMessage;
        Optional<StackTraceElement> optionalStackTraceElement = Arrays.stream(exception.getStackTrace()).findFirst();
        if (optionalStackTraceElement.isPresent()) {
            StackTraceElement traceElement = optionalStackTraceElement.get();
            exceptionMessage = String.format(
                    "%s happened in %s.%s():%s",
                    exception.getClass(),
                    traceElement.getClassName(),
                    traceElement.getMethodName(),
                    traceElement.getLineNumber()
            );
        } else exceptionMessage = exception.getMessage();
        return exceptionMessage;
    }

    private void log(Object code, String responseCode) {
        log.error(
                LOG_PATTERN,
                code,
                responseCode
        );
    }

    private static String getLangHeader(HttpServletRequest request) {
        String header = request.getHeader("accept-language");
        return header == null ? "en" : header;
    }
}