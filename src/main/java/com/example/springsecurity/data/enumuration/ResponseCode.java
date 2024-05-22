package com.example.springsecurity.data.enumuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCode {
    // successful response
    OK_OPERATION(1, "ok.message.successful_operation"),


    // client api error
    ACCESS_DENIED(403, "errors.message.access_denied"),
    INVALID_PATH(404, "errors.message.invalid_path"),
    METHOD_NOT_ALLOWED(405, "errors.message.invalid_mapping_http_method"),
    INVALID_JSON_STRUCTURE(406, "errors.message.invalid_json_structure"),
    INVALID_CONTENT_TYPE(407, "errors.message.invalid_media_type"),
    MISS_REQUEST_PARAM(408, "errors.message.miss_req_param"),
    INCORRECT_USERNAME_OR_PASSWORD(409, "errors.message.incorrect_username_pass"),
    TOKEN_NOT_VALID(20119, "errors.message.token_not_valid"),
    INCORRECT_TOKEN(412, "errors.message.incorrect_token"),

    // internal error
    INTERNAL_APP_ERROR(5000, DuplicateMessage.INTERNAL_ERROR),
    INTERNAL_INPUT_MISMATCHED_JSON_VALUE(5002, DuplicateMessage.INTERNAL_ERROR),
    INTERNAL_CONNECTION_TIMEOUT(5003, DuplicateMessage.INTERNAL_ERROR),
    INTERNAL_CALLING_KEYCLOAK_SERVER(5008, "errors.message.keycloak_err"),

    ;


    private final int code;
    private final String description;

    public static ResponseCode getValue(int code) {
        ResponseCode[] enums = ResponseCode.values();
        for (ResponseCode item : enums) {
            if (item.code == code)
                return item;
        }
        return null;
    }

    public static class DuplicateMessage {
        public static final String INTERNAL_ERROR = "errors.message.internal_error";

        private DuplicateMessage() {
        }
    }
}
