package com.yupi.yuojbackendmodel.model.codesandbox.judge0;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("source_code")
    private String source_code;
    
    /**
     * 编程语言ID (Judge0语言ID)
     */
    @JsonProperty("language_id")
    private Integer language_id;
    
    /**
     * 标准输入
     */
    private String stdin;
    
    /**
     * 期望输出
     */
    @JsonProperty("expected_output")
    private String expected_output;
    
    /**
     * CPU时间限制（秒）
     */
    @JsonProperty("cpu_time_limit")
    private Integer cpu_time_limit;
    
    /**
     * 内存限制（KB）
     */
    @JsonProperty("memory_limit")
    private Integer memory_limit;
    
    /**
     * 是否启用网络
     */
    @JsonProperty("enable_network")
    private Boolean enable_network;
} 