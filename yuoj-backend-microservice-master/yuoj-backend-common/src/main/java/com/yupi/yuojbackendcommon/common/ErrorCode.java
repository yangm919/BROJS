package com.yupi.yuojbackendcommon.common;
/**
 * Custom error codes
 *
 */
public enum ErrorCode {
    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "Request parameter error"),
    NOT_LOGIN_ERROR(40100, "Not logged in"),
    NO_AUTH_ERROR(40101, "No permission"),
    NOT_FOUND_ERROR(40400, "Request data does not exist"),
    FORBIDDEN_ERROR(40300, "Access forbidden"),
    SYSTEM_ERROR(50000, "System internal exception"),
    OPERATION_ERROR(50001, "Operation failed"),
    API_REQUEST_ERROR(50010, "API call failed");
    /**
     * Status code
     */
    private final int code;
    /**
     * Message
     */
    private final String message;
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    public int getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}
