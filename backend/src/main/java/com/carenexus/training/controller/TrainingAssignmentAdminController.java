package com.carenexus.training.controller;

import com.carenexus.common.response.ApiResponse;
import com.carenexus.training.dto.AssignmentImportPreviewResponse;
import com.carenexus.training.dto.AssignmentPublishRequest;
import com.carenexus.training.resource.support.TrainingResourceAccessPolicy;
import com.carenexus.training.service.AssignmentDocxParser;
import com.carenexus.training.service.TrainingAssignmentAdminService;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@ConditionalOnProperty(name = "care-nexus.course-interactions.enabled", havingValue = "true", matchIfMissing = true)
@RequestMapping("/api/v1/training/assignments")
public class TrainingAssignmentAdminController {
    private final TrainingResourceAccessPolicy accessPolicy;
    private final AssignmentDocxParser parser;
    private final TrainingAssignmentAdminService assignmentService;

    public TrainingAssignmentAdminController(TrainingResourceAccessPolicy accessPolicy,
            AssignmentDocxParser parser, TrainingAssignmentAdminService assignmentService) {
        this.accessPolicy = accessPolicy;
        this.parser = parser;
        this.assignmentService = assignmentService;
    }

    @PostMapping("/import-preview")
    public ApiResponse<AssignmentImportPreviewResponse> preview(@RequestParam("file") MultipartFile file)
            throws IOException {
        accessPolicy.requireManage();
        return ApiResponse.success(parser.parse(file));
    }

    @PostMapping("/publish")
    public ApiResponse<Map<String, Integer>> publish(@Valid @RequestBody AssignmentPublishRequest request) {
        return ApiResponse.success(Collections.singletonMap("publishedCount", assignmentService.publish(request)));
    }
}
