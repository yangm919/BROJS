package com.yupi.yuojcodesandbox.model;

import lombok.Data;

/**
 * Process execution information
 */
@Data
public class ExecuteMessage {

    private Integer exitValue;

    private String message;

    private String errorMessage;

    private Long time;

    private Long memory;
}
