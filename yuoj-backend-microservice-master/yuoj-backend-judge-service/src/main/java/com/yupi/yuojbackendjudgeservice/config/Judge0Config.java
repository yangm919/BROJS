package com.yupi.yuojbackendjudgeservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Judge0 API 配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "codesandbox.judge0")
public class Judge0Config {
    
    /**
     * Judge0 API 基础URL
     */
    private String baseUrl = "http://4.213.48.226:2358/";
    
    /**
     * RapidAPI 密钥
     */
    private String rapidapiKey = "087d919e2dmshf5f3c038d699943p1fb696jsnf1a52bbb84b2";
    
    /**
     * RapidAPI 主机
     */
    private String rapidapiHost = "4.213.48.226:2358";
    
    /**
     * 超时时间（秒）
     */
    private Integer timeout = 30;
    
    /**
     * 最大重试次数
     */
    private Integer maxRetries = 30;
} 