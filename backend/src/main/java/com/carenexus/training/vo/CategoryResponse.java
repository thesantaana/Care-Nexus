package com.carenexus.training.vo;

public class CategoryResponse {

    private Long id;
    private String categoryName;
    private Integer sortNo;
    private String status;

    public CategoryResponse() {
    }

    public CategoryResponse(Long id, String categoryName, Integer sortNo, String status) {
        this.id = id;
        this.categoryName = categoryName;
        this.sortNo = sortNo;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public String getStatus() {
        return status;
    }
}
