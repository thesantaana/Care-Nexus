package com.carenexus.training.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ExamAnswerRequest {

    @NotNull
    private Long questionId;

    @NotBlank
    private String answer;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
