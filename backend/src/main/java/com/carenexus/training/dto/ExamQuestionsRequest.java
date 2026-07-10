package com.carenexus.training.dto;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

public class ExamQuestionsRequest {

    @Valid
    @NotEmpty
    private List<ExamQuestionRequest> questions = new ArrayList<>();

    public List<ExamQuestionRequest> getQuestions() {
        return questions;
    }

    public void setQuestions(List<ExamQuestionRequest> questions) {
        this.questions = questions;
    }
}
