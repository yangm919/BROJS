package com.yupi.yuojbackendmodel.model.codesandbox.judge0;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Judge0 API 提交响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Judge0SubmissionResponse {
    /**
     * 提交token
     */
    private String token;
} 