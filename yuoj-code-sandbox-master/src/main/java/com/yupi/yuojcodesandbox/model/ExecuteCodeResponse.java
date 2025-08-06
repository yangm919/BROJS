package com.yupi.yuojcodesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse {

    private List<String> outputList;

    /**
     * Interface information
     */
    private String message;

    /**
     * Execution status
     */
    private Integer status;

    /**
     * Judge information
     */
    private JudgeInfo judgeInfo;
}
