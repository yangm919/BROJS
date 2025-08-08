package com.yupi.yuojbackendquestionservice.controller.inner;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.yuojbackendmodel.model.entity.Question;
import com.yupi.yuojbackendmodel.model.entity.QuestionSubmit;
import com.yupi.yuojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.yupi.yuojbackendmodel.model.vo.UserStatisticsVO;
import com.yupi.yuojbackendquestionservice.service.QuestionService;
import com.yupi.yuojbackendquestionservice.service.QuestionSubmitService;
import com.yupi.yuojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
/**
 * 该服务仅供内部调用
 */
@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionSubmitService questionSubmitService;
    @GetMapping("/get/id")
    @Override
    public Question getQuestionById(@RequestParam("questionId") long questionId) {
        return questionService.getById(questionId);
    }
    @GetMapping("/question_submit/get/id")
    @Override
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionId") long questionSubmitId) {
        return questionSubmitService.getById(questionSubmitId);
    }
    @PostMapping("/question_submit/update")
    @Override
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }
    @PostMapping("/update")
    @Override
    public boolean updateQuestionById(@RequestBody Question question) {
        return questionService.updateById(question);
    }
    /**
     * 获取用户统计信息
     *
     * @param userId 用户ID
     * @return 用户统计信息
     */
    @GetMapping("/get/user/statistics")
    @Override
    public UserStatisticsVO getUserStatistics(@RequestParam("userId") long userId) {
        UserStatisticsVO statisticsVO = new UserStatisticsVO();
        statisticsVO.setUserId(userId);
        // 获取用户提交总数
        QueryWrapper<QuestionSubmit> submitQuery = new QueryWrapper<>();
        submitQuery.eq("userId", userId);
        submitQuery.eq("isDelete", 0);
        long totalSubmissions = questionSubmitService.count(submitQuery);
        statisticsVO.setTotalSubmissions(totalSubmissions);
        // 获取用户正确提交数
        QueryWrapper<QuestionSubmit> acceptedQuery = new QueryWrapper<>();
        acceptedQuery.eq("userId", userId);
        acceptedQuery.eq("status", QuestionSubmitStatusEnum.SUCCEED.getValue());
        acceptedQuery.eq("isDelete", 0);
        long acceptedSubmissions = questionSubmitService.count(acceptedQuery);
        statisticsVO.setAcceptedSubmissions(acceptedSubmissions);
        // 计算正确率
        double acceptanceRate = totalSubmissions > 0 ? (double) acceptedSubmissions / totalSubmissions : 0.0;
        statisticsVO.setAcceptanceRate(acceptanceRate);
        // 获取用户做题数量（不同题目的数量）
        QueryWrapper<QuestionSubmit> questionQuery = new QueryWrapper<>();
        questionQuery.select("DISTINCT questionId");
        questionQuery.eq("userId", userId);
        questionQuery.eq("isDelete", 0);
        List<QuestionSubmit> questionSubmits = questionSubmitService.list(questionQuery);
        long totalQuestions = questionSubmits.size();
        statisticsVO.setTotalQuestions(totalQuestions);
        // 获取用户创建的题目数量
        QueryWrapper<Question> createdQuery = new QueryWrapper<>();
        createdQuery.eq("userId", userId);
        createdQuery.eq("isDelete", 0);
        long createdQuestions = questionService.count(createdQuery);
        statisticsVO.setCreatedQuestions(createdQuestions);
        return statisticsVO;
    }
}
