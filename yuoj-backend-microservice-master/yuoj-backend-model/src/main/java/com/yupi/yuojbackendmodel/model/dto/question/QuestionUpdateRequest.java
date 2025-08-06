package com.yupi.yuojbackendmodel.model.dto.question;
import lombok.Data;
import java.io.Serializable;
import java.util.List;
/**
 * Update request
 *
 */
@Data
public class QuestionUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * Title
     */
    private String title;
    /**
     * Content
     */
    private String content;
    /**
     * Tag list
     */
    private List<String> tags;
    /**
     * Question answer
     */
    private String answer;
    /**
     * Judge cases
     */
    private List<JudgeCase> judgeCase;
    /**
     * Judge configuration
     */
    private JudgeConfig judgeConfig;
    private static final long serialVersionUID = 1L;
}