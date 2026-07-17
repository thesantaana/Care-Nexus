package com.carenexus.training.vo;

import java.util.ArrayList;
import java.util.List;

public class LearningLibraryResponse {
    private List<Long> courseResourceIds = new ArrayList<>();
    private List<Long> completedResourceIds = new ArrayList<>();
    private List<Long> favoriteResourceIds = new ArrayList<>();

    public List<Long> getCourseResourceIds() { return courseResourceIds; }

    public void setCourseResourceIds(List<Long> ids) { courseResourceIds = ids; }

    public List<Long> getCompletedResourceIds() { return completedResourceIds; }

    public void setCompletedResourceIds(List<Long> ids) { completedResourceIds = ids; }

    public List<Long> getFavoriteResourceIds() { return favoriteResourceIds; }

    public void setFavoriteResourceIds(List<Long> ids) { favoriteResourceIds = ids; }
}
