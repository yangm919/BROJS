package com.yupi.yuojbackendmodel.model.codesandbox.judge0;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Judge0 API 提交请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Judge0SubmissionRequest {
    /**
     * 源代码
     */
    private String source_code;
    
    /**
     * 编程语言ID (Judge0语言ID)
     */
    private Integer language_id;
    
    /**
     * 标准输入
     */
    private String stdin;
    
    /**
     * 期望输出
     */
    private String expected_output;
    
    /**
     * CPU时间限制（秒）
     */
    private Integer cpu_time_limit;
    
    /**
     * 内存限制（KB）
     */
    private Integer memory_limit;
    
    /**
     * 是否启用网络
     */
    private Boolean enable_network;
} 