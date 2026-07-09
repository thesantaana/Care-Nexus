package com.carenexus.training.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("training_resource_tag")
public class TrainingResourceTag {

    private Long id;
    private Long resourceId;
    private Long tagId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
}
