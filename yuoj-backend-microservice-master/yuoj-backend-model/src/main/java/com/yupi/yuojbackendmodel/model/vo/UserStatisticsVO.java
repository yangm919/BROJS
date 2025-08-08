package com.yupi.yuojbackendmodel.model.vo;
import lombok.Data;
import java.io.Serializable;
/**
 * 用户统计信息视图
 */
@Data
public class UserStatisticsVO implements Serializable {
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 提交题目总数
     */
    private Long totalSubmissions;
    /**
     * 做题数量（不同题目的数量）
     */
    private Long totalQuestions;
    /**
     * 正确提交数量
     */
    private Long acceptedSubmissions;
    /**
     * 正确率
     */
    private Double acceptanceRate;
    /**
     * 创建题目数量
     */
    private Long createdQuestions;
    private static final long serialVersionUID = 1L;
} 