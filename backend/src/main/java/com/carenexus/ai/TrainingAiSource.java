package com.carenexus.ai;

public class TrainingAiSource {
    private Long resourceId;
    private String title;
    private String content;

    public TrainingAiSource(Long resourceId, String title, String content) {
        this.resourceId = resourceId;
        this.title = title;
        this.content = content;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
