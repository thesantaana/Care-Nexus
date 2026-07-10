package com.carenexus.training.dto;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

public class QuestionOptionsRequest {

    @Valid
    @NotEmpty
    private List<QuestionOptionRequest> options = new ArrayList<>();

    public List<QuestionOptionRequest> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionOptionRequest> options) {
        this.options = options;
    }
}
