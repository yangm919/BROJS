package com.yupi.yuojbackendmodel.model.codesandbox.judge0;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Judge0 API 结果响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Judge0ResultResponse {
    /**
     * 提交token
     */
    private String token;
    
    /**
     * 状态对象
     */
    private Judge0Status status;
    
    /**
     * 标准输出
     */
    private String stdout;
    
    /**
     * 标准错误
     */
    private String stderr;
    
    /**
     * 编译输出
     */
    @JsonProperty("compile_output")
    private String compile_output;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 时间（秒）
     */
    private String time;
    
    /**
     * 内存（KB）
     */
    private Integer memory;
    
    /**
     * 退出代码
     */
    @JsonProperty("exit_code")
    private Integer exit_code;
    
    /**
     * 退出信号
     */
    @JsonProperty("exit_signal")
    private Integer exit_signal;
    
    /**
     * Judge0状态内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Judge0Status {
        /**
         * 状态ID
         */
        private Integer id;
        
        /**
         * 状态描述
         */
        private String description;
    }
} 