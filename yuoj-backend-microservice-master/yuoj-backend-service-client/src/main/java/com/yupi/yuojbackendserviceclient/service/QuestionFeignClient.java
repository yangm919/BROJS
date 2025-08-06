package com.yupi.yuojbackendserviceclient.service;
import com.yupi.yuojbackendmodel.model.entity.Question;
import com.yupi.yuojbackendmodel.model.entity.QuestionSubmit;
import com.yupi.yuojbackendmodel.model.vo.UserStatisticsVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
/**
* @description Database operation Service for table [question]
*/
@FeignClient(name = "yuoj-backend-question-service", path = "/api/question/inner")
public interface QuestionFeignClient {
    @GetMapping("/get/id")
    Question getQuestionById(@RequestParam("questionId") long questionId);
    @GetMapping("/question_submit/get/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("questionId") long questionSubmitId);
    @PostMapping("/question_submit/update")
    boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit);
    @PostMapping("/update")
    boolean updateQuestionById(@RequestBody Question question);
    /**
     * Get user statistics
     *
     * @param userId User ID
     * @return User statistics
     */
    @GetMapping("/get/user/statistics")
    UserStatisticsVO getUserStatistics(@RequestParam("userId") long userId);
}
