package com.carenexus.ai;

public class TrainingAiResult {

    private String content;
    private boolean draft;

    public TrainingAiResult(String content, boolean draft) {
        this.content = content;
        this.draft = draft;
    }

    public String getContent() {
        return content;
    }

    public boolean isDraft() {
        return draft;
    }
}
