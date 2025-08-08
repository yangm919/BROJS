package com.yupi.yuojbackendcommon.exception;
import com.yupi.yuojbackendcommon.common.ErrorCode;
/**
 * Exception throwing utility class
 */
public class ThrowUtils {
    /**
     * Throw exception if condition is true
     *
     * @param condition
     * @param runtimeException
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }
    /**
     * Throw exception if condition is true
     *
     * @param condition
     * @param errorCode
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }
    /**
     * Throw exception if condition is true
     *
     * @param condition
     * @param errorCode
     * @param message
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }
}
