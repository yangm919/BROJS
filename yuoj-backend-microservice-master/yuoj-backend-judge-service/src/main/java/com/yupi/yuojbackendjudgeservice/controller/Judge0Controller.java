package com.yupi.yuojbackendjudgeservice.controller;

import com.yupi.yuojbackendcommon.common.BaseResponse;
import com.yupi.yuojbackendcommon.common.ResultUtils;
import com.yupi.yuojbackendjudgeservice.judge.Judge0JudgeService;
import com.yupi.yuojbackendmodel.model.entity.QuestionSubmit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Judge0判题控制器
 */
@RestController
@RequestMapping("/api/judge0")
@Slf4j
public class Judge0Controller {

    @Resource
    private Judge0JudgeService judge0JudgeService;

    /**
     * 判题
     * @param questionSubmitId 题目提交ID
     * @return 判题结果
     */
    @PostMapping("/do")
    public BaseResponse<QuestionSubmit> doJudge(@RequestParam long questionSubmitId) {
        log.info("收到判题请求，题目提交ID: {}", questionSubmitId);
        
        try {
            QuestionSubmit result = judge0JudgeService.doJudgeWithJudge0(questionSubmitId);
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("判题失败", e);
            return ResultUtils.error(500, "判题失败: " + e.getMessage());
        }
    }

    /**
     * 批量判题
     * @param questionSubmitIds 题目提交ID列表
     * @return 操作结果
     */
    @PostMapping("/batch")
    public BaseResponse<String> batchJudge(@RequestBody List<Long> questionSubmitIds) {
        log.info("收到批量判题请求，数量: {}", questionSubmitIds.size());
        
        try {
            judge0JudgeService.batchJudge(questionSubmitIds);
            return ResultUtils.success("批量判题已启动");
        } catch (Exception e) {
            log.error("批量判题失败", e);
            return ResultUtils.error(500, "批量判题失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查
     * @return 服务状态
     */
    @GetMapping("/health")
    public BaseResponse<String> health() {
        return ResultUtils.success("Judge0判题服务运行正常");
    }
} 