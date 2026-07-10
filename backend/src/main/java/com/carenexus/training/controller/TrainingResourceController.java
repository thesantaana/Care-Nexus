package com.carenexus.training.controller;

import com.carenexus.common.response.ApiResponse;
import com.carenexus.common.response.PageResponse;
import com.carenexus.training.dto.CategoryRequest;
import com.carenexus.training.dto.OfflineRequest;
import com.carenexus.training.dto.ResourceRequest;
import com.carenexus.training.dto.StatusRequest;
import com.carenexus.training.dto.TagRequest;
import com.carenexus.training.service.TrainingResourceService;
import com.carenexus.training.vo.CategoryResponse;
import com.carenexus.training.vo.ResourceResponse;
import com.carenexus.training.vo.ResourceSummaryResponse;
import com.carenexus.training.vo.TagResponse;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/training")
public class TrainingResourceController {

    private final TrainingResourceService trainingResourceService;

    public TrainingResourceController(TrainingResourceService trainingResourceService) {
        this.trainingResourceService = trainingResourceService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryResponse>> listCategories(@RequestParam(required = false) String status) {
        return ApiResponse.success(trainingResourceService.listCategories(status));
    }

    @PostMapping("/categories")
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ApiResponse.success(trainingResourceService.createCategory(request));
    }

    @PutMapping("/categories/{id}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return ApiResponse.success(trainingResourceService.updateCategory(id, request));
    }

    @PutMapping("/categories/{id}/status")
    public ApiResponse<CategoryResponse> updateCategoryStatus(@PathVariable Long id,
            @Valid @RequestBody StatusRequest request) {
        return ApiResponse.success(trainingResourceService.updateCategoryStatus(id, request));
    }

    @GetMapping("/tags")
    public ApiResponse<List<TagResponse>> listTags(@RequestParam(required = false) String status) {
        return ApiResponse.success(trainingResourceService.listTags(status));
    }

    @PostMapping("/tags")
    public ApiResponse<TagResponse> createTag(@Valid @RequestBody TagRequest request) {
        return ApiResponse.success(trainingResourceService.createTag(request));
    }

    @PutMapping("/tags/{id}")
    public ApiResponse<TagResponse> updateTag(@PathVariable Long id, @Valid @RequestBody TagRequest request) {
        return ApiResponse.success(trainingResourceService.updateTag(id, request));
    }

    @PutMapping("/tags/{id}/status")
    public ApiResponse<TagResponse> updateTagStatus(@PathVariable Long id,
            @Valid @RequestBody StatusRequest request) {
        return ApiResponse.success(trainingResourceService.updateTagStatus(id, request));
    }

    @PostMapping("/resources")
    public ApiResponse<ResourceResponse> createResource(@Valid @RequestBody ResourceRequest request) {
        return ApiResponse.success(trainingResourceService.createResource(request));
    }

    @PutMapping("/resources/{id}")
    public ApiResponse<ResourceResponse> updateResource(@PathVariable Long id,
            @Valid @RequestBody ResourceRequest request) {
        return ApiResponse.success(trainingResourceService.updateResource(id, request));
    }

    @GetMapping("/resources")
    public ApiResponse<PageResponse<ResourceSummaryResponse>> pageResources(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") @Min(1) int pageNo,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize) {
        return ApiResponse.success(trainingResourceService.pageResources(keyword, resourceType, categoryId, tagId,
                status, pageNo, pageSize));
    }

    @GetMapping("/resources/{id}")
    public ApiResponse<ResourceResponse> getResource(@PathVariable Long id) {
        return ApiResponse.success(trainingResourceService.getResource(id));
    }

    @PutMapping("/resources/{id}/publish")
    public ApiResponse<ResourceResponse> publishResource(@PathVariable Long id) {
        return ApiResponse.success(trainingResourceService.publishResource(id));
    }

    @PutMapping("/resources/{id}/offline")
    public ApiResponse<ResourceResponse> offlineResource(@PathVariable Long id,
            @Valid @RequestBody OfflineRequest request) {
        return ApiResponse.success(trainingResourceService.offlineResource(id, request));
    }
}
