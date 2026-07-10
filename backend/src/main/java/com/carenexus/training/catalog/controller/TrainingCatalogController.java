package com.carenexus.training.catalog.controller;

import com.carenexus.common.response.ApiResponse;
import com.carenexus.training.catalog.service.TrainingCatalogService;
import com.carenexus.training.dto.CategoryRequest;
import com.carenexus.training.dto.StatusRequest;
import com.carenexus.training.dto.TagRequest;
import com.carenexus.training.vo.CategoryResponse;
import com.carenexus.training.vo.TagResponse;
import java.util.List;
import javax.validation.Valid;
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
public class TrainingCatalogController {

    private final TrainingCatalogService trainingCatalogService;

    public TrainingCatalogController(TrainingCatalogService trainingCatalogService) {
        this.trainingCatalogService = trainingCatalogService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryResponse>> listCategories(@RequestParam(required = false) String status) {
        return ApiResponse.success(trainingCatalogService.listCategories(status));
    }

    @PostMapping("/categories")
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ApiResponse.success(trainingCatalogService.createCategory(request));
    }

    @PutMapping("/categories/{id}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return ApiResponse.success(trainingCatalogService.updateCategory(id, request));
    }

    @PutMapping("/categories/{id}/status")
    public ApiResponse<CategoryResponse> updateCategoryStatus(@PathVariable Long id,
            @Valid @RequestBody StatusRequest request) {
        return ApiResponse.success(trainingCatalogService.updateCategoryStatus(id, request));
    }

    @GetMapping("/tags")
    public ApiResponse<List<TagResponse>> listTags(@RequestParam(required = false) String status) {
        return ApiResponse.success(trainingCatalogService.listTags(status));
    }

    @PostMapping("/tags")
    public ApiResponse<TagResponse> createTag(@Valid @RequestBody TagRequest request) {
        return ApiResponse.success(trainingCatalogService.createTag(request));
    }

    @PutMapping("/tags/{id}")
    public ApiResponse<TagResponse> updateTag(@PathVariable Long id, @Valid @RequestBody TagRequest request) {
        return ApiResponse.success(trainingCatalogService.updateTag(id, request));
    }

    @PutMapping("/tags/{id}/status")
    public ApiResponse<TagResponse> updateTagStatus(@PathVariable Long id,
            @Valid @RequestBody StatusRequest request) {
        return ApiResponse.success(trainingCatalogService.updateTagStatus(id, request));
    }
}
