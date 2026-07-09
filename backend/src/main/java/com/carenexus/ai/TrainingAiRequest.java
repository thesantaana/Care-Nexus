package com.carenexus.ai;

import java.util.ArrayList;
import java.util.List;

public class TrainingAiRequest {

    private List<Long> sourceResourceIds = new ArrayList<>();
    private String prompt;

    public List<Long> getSourceResourceIds() {
        return sourceResourceIds;
    }

    public void setSourceResourceIds(List<Long> sourceResourceIds) {
        this.sourceResourceIds = sourceResourceIds;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
