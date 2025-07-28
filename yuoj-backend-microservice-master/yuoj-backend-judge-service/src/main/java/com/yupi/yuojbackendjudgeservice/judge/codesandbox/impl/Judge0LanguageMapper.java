package com.yupi.yuojbackendjudgeservice.judge.codesandbox.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * Judge0 语言映射工具类
 * 将编程语言名称映射到Judge0的语言ID
 */
public class Judge0LanguageMapper {
    
    private static final Map<String, Integer> LANGUAGE_MAP = new HashMap<>();
    
    static {
        // 常用编程语言映射
        LANGUAGE_MAP.put("java", 62);        // Java (OpenJDK 13.0.1)
        LANGUAGE_MAP.put("python", 71);      // Python (3.8.1)
        LANGUAGE_MAP.put("python3", 71);     // Python (3.8.1)
        LANGUAGE_MAP.put("cpp", 54);         // C++ (GCC 9.2.0)
        LANGUAGE_MAP.put("c++", 54);         // C++ (GCC 9.2.0)
        LANGUAGE_MAP.put("c", 50);           // C (GCC 9.2.0)
        LANGUAGE_MAP.put("javascript", 63);  // JavaScript (Node.js 12.14.0)
        LANGUAGE_MAP.put("js", 63);          // JavaScript (Node.js 12.14.0)
        LANGUAGE_MAP.put("go", 60);          // Go (1.13.5)
        LANGUAGE_MAP.put("rust", 73);        // Rust (1.40.0)
        LANGUAGE_MAP.put("php", 68);         // PHP (7.4.1)
        LANGUAGE_MAP.put("ruby", 72);        // Ruby (2.7.0)
        LANGUAGE_MAP.put("swift", 83);       // Swift (5.2.3)
        LANGUAGE_MAP.put("kotlin", 78);      // Kotlin (1.3.70)
        LANGUAGE_MAP.put("scala", 81);       // Scala (2.13.2)
        LANGUAGE_MAP.put("typescript", 74);  // TypeScript (3.7.4)
        LANGUAGE_MAP.put("ts", 74);          // TypeScript (3.7.4)
    }
    
    /**
     * 获取语言ID
     * @param language 编程语言名称
     * @return Judge0语言ID，如果未找到则返回Java的ID作为默认值
     */
    public static Integer getLanguageId(String language) {
        if (language == null || language.trim().isEmpty()) {
            return 62; // 默认返回Java
        }
        return LANGUAGE_MAP.getOrDefault(language.toLowerCase().trim(), 62);
    }
    
    /**
     * 检查是否支持该语言
     * @param language 编程语言名称
     * @return 是否支持
     */
    public static boolean isSupported(String language) {
        if (language == null || language.trim().isEmpty()) {
            return false;
        }
        return LANGUAGE_MAP.containsKey(language.toLowerCase().trim());
    }
} 