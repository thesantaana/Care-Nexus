package com.carenexus.training.vo;

public class CourseFavoriteResponse {
    private Long resourceId;
    private Boolean favorite;

    public Long getResourceId() { return resourceId; }

    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }

    public Boolean getFavorite() { return favorite; }

    public void setFavorite(Boolean favorite) { this.favorite = favorite; }
}
