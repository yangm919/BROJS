package com.yupi.yuojbackendmodel.model.codesandbox.judge0;

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
     * 状态ID
     */
    private Integer status_id;
    
    /**
     * 状态描述
     */
    private String status_description;
    
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
    private String compile_output;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 时间（秒）
     */
    private Double time;
    
    /**
     * 内存（KB）
     */
    private Integer memory;
    
    /**
     * 退出代码
     */
    private Integer exit_code;
    
    /**
     * 退出信号
     */
    private Integer exit_signal;
} 