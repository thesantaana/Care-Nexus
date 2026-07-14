package com.carenexus.training.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class DiscussionRequest {
    @NotBlank
    @Size(max = 160)
    private String title;
    @NotBlank
    @Size(max = 5000)
    private String content;

    public String getTitle() { return title; }

    public void setTitle(String value) { title = value; }

    public String getContent() { return content; }

    public void setContent(String value) { content = value; }
}

