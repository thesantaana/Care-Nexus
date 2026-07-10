package com.carenexus.training.vo;

public class TagResponse {

    private Long id;
    private String tagName;
    private String status;

    public TagResponse() {
    }

    public TagResponse(Long id, String tagName, String status) {
        this.id = id;
        this.tagName = tagName;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getTagName() {
        return tagName;
    }

    public String getStatus() {
        return status;
    }
}
