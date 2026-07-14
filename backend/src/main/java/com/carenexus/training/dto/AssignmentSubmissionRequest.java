package com.carenexus.training.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AssignmentSubmissionRequest {
    @NotBlank
    @Size(max = 10000)
    private String answer;

    public String getAnswer() { return answer; }

    public void setAnswer(String value) { answer = value; }
}

