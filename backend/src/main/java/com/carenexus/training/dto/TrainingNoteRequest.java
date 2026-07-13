package com.carenexus.training.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class TrainingNoteRequest {
    @NotBlank
    @Size(max = 120)
    private String title;

    @Size(max = 200000)
    private String content;

    public String getTitle() { return title; }

    public void setTitle(String value) { title = value; }

    public String getContent() { return content; }

    public void setContent(String value) { content = value; }
}
