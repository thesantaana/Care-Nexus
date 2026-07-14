package com.carenexus.training.resource.controller;

import com.carenexus.common.response.ApiResponse;
import com.carenexus.common.response.PageResponse;
import com.carenexus.file.FileStorageService;
import com.carenexus.file.StoredFile;
import com.carenexus.training.dto.OfflineRequest;
import com.carenexus.training.dto.ResourceRequest;
import com.carenexus.training.resource.service.TrainingResourceQueryService;
import com.carenexus.training.resource.service.TrainingResourceService;
import com.carenexus.training.resource.support.TrainingResourceAccessPolicy;
import com.carenexus.training.vo.ResourceResponse;
import com.carenexus.training.vo.ResourceSummaryResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
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
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/api/v1/training/resources")
public class TrainingResourceController {

    private final TrainingResourceService trainingResourceService;
    private final TrainingResourceQueryService trainingResourceQueryService;
    private final TrainingResourceAccessPolicy accessPolicy;
    private final FileStorageService fileStorageService;

    public TrainingResourceController(TrainingResourceService trainingResourceService,
            TrainingResourceQueryService trainingResourceQueryService,
            TrainingResourceAccessPolicy accessPolicy,
            FileStorageService fileStorageService) {
        this.trainingResourceService = trainingResourceService;
        this.trainingResourceQueryService = trainingResourceQueryService;
        this.accessPolicy = accessPolicy;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/covers")
    public ApiResponse<Map<String, String>> uploadCover(@RequestParam("file") MultipartFile file)
            throws IOException {
        accessPolicy.requireManage();
        try (InputStream input = file.getInputStream()) {
            StoredFile stored = fileStorageService.store(file.getOriginalFilename(), file.getContentType(), input,
                    file.getSize());
            return ApiResponse.success(Collections.singletonMap("url", "/note-media/" + stored.getStorageName()));
        }
    }

    @PostMapping
    public ApiResponse<ResourceResponse> createResource(@Valid @RequestBody ResourceRequest request) {
        return ApiResponse.success(trainingResourceService.createResource(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ResourceResponse> updateResource(@PathVariable Long id,
            @Valid @RequestBody ResourceRequest request) {
        return ApiResponse.success(trainingResourceService.updateResource(id, request));
    }

    @GetMapping
    public ApiResponse<PageResponse<ResourceSummaryResponse>> pageResources(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") @Min(1) int pageNo,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize) {
        return ApiResponse.success(trainingResourceQueryService.pageResources(keyword, resourceType, categoryId, tagId,
                status, pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<ResourceResponse> getResource(@PathVariable Long id) {
        return ApiResponse.success(trainingResourceQueryService.getResource(id));
    }

    @PutMapping("/{id}/publish")
    public ApiResponse<ResourceResponse> publishResource(@PathVariable Long id) {
        return ApiResponse.success(trainingResourceService.publishResource(id));
    }

    @PutMapping("/{id}/offline")
    public ApiResponse<ResourceResponse> offlineResource(@PathVariable Long id,
            @Valid @RequestBody OfflineRequest request) {
        return ApiResponse.success(trainingResourceService.offlineResource(id, request));
    }
}
