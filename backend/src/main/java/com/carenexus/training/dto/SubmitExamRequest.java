package com.carenexus.training.dto;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

public class SubmitExamRequest {

    @Valid
    @NotEmpty
    private List<ExamAnswerRequest> answers = new ArrayList<>();

    public List<ExamAnswerRequest> getAnswers() {
        return answers;
    }

    public void setAnswers(List<ExamAnswerRequest> answers) {
        this.answers = answers;
    }
}
