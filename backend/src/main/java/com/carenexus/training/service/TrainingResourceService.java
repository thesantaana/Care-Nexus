package com.carenexus.training.service;

import com.carenexus.common.response.PageResponse;
import com.carenexus.training.dto.CategoryRequest;
import com.carenexus.training.dto.OfflineRequest;
import com.carenexus.training.dto.ResourceRequest;
import com.carenexus.training.dto.StatusRequest;
import com.carenexus.training.dto.TagRequest;
import com.carenexus.training.vo.CategoryResponse;
import com.carenexus.training.vo.ResourceResponse;
import com.carenexus.training.vo.ResourceSummaryResponse;
import com.carenexus.training.vo.TagResponse;
import java.util.List;

public interface TrainingResourceService {

    List<CategoryResponse> listCategories(String status);

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    CategoryResponse updateCategoryStatus(Long id, StatusRequest request);

    List<TagResponse> listTags(String status);

    TagResponse createTag(TagRequest request);

    TagResponse updateTag(Long id, TagRequest request);

    TagResponse updateTagStatus(Long id, StatusRequest request);

    ResourceResponse createResource(ResourceRequest request);

    ResourceResponse updateResource(Long id, ResourceRequest request);

    PageResponse<ResourceSummaryResponse> pageResources(String keyword, String resourceType, Long categoryId,
            Long tagId, String status, int pageNo, int pageSize);

    ResourceResponse getResource(Long id);

    ResourceResponse publishResource(Long id);

    ResourceResponse offlineResource(Long id, OfflineRequest request);
}
