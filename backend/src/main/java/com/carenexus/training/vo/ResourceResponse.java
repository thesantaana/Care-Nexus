package com.carenexus.training.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResourceResponse {

    private Long id;
    private String resourceType;
    private String storageMode;
    private Long categoryId;
    private CategoryResponse category;
    private String title;
    private String summary;
    private String content;
    private Long fileResourceId;
    private String externalUrl;
    private Integer durationSeconds;
    private String status;
    private LocalDateTime publishedAt;
    private UserBriefResponse createdBy;
    private UserBriefResponse updatedBy;
    private List<TagResponse> tags = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getStorageMode() {
        return storageMode;
    }

    public void setStorageMode(String storageMode) {
        this.storageMode = storageMode;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public CategoryResponse getCategory() {
        return category;
    }

    public void setCategory(CategoryResponse category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getFileResourceId() {
        return fileResourceId;
    }

    public void setFileResourceId(Long fileResourceId) {
        this.fileResourceId = fileResourceId;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public UserBriefResponse getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserBriefResponse createdBy) {
        this.createdBy = createdBy;
    }

    public UserBriefResponse getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UserBriefResponse updatedBy) {
        this.updatedBy = updatedBy;
    }

    public List<TagResponse> getTags() {
        return tags;
    }

    public void setTags(List<TagResponse> tags) {
        this.tags = tags;
    }
}
