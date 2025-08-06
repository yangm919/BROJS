package com.yupi.yuojcodesandbox.model;

import lombok.Data;

/**
 * Judge information
 */
@Data
public class JudgeInfo {

    /**
     * Program execution information
     */
    private String message;

    /**
     * Memory consumption
     */
    private Long memory;

    /**
     * Time consumption (KB)
     */
    private Long time;
}
