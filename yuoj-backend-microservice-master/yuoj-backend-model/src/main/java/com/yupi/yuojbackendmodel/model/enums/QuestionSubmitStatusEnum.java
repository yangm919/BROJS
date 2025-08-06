package com.yupi.yuojbackendmodel.model.enums;
import org.apache.commons.lang3.ObjectUtils;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Question submission enumeration
 *
 */
public enum QuestionSubmitStatusEnum {
    // 0 - Waiting for judgment, 1 - Judging, 2 - Success, 3 - Failed
    WAITING("Waiting", 0),
    RUNNING("Judging", 1),
    SUCCEED("Success", 2),
    FAILED("Failed", 3);
    private final String text;
    private final Integer value;
    QuestionSubmitStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }
    /**
     * Get value list
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }
    /**
     * Get enum by value
     *
     * @param value
     * @return
     */
    public static QuestionSubmitStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (QuestionSubmitStatusEnum anEnum : QuestionSubmitStatusEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
    public Integer getValue() {
        return value;
    }
    public String getText() {
        return text;
    }
}
