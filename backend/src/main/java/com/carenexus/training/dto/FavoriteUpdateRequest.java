package com.carenexus.training.dto;

import javax.validation.constraints.NotNull;

public class FavoriteUpdateRequest {
    @NotNull
    private Boolean favorite;

    public Boolean getFavorite() { return favorite; }

    public void setFavorite(Boolean favorite) { this.favorite = favorite; }
}
