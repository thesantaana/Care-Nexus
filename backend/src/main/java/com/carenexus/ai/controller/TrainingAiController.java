package com.carenexus.ai.controller;

import com.carenexus.ai.dto.TrainingAiRequests.ContentRequest;
import com.carenexus.ai.dto.TrainingAiRequests.QuestionDraftRequest;
import com.carenexus.ai.dto.TrainingAiRequests.ReviewRequest;
import com.carenexus.ai.service.AiQuestionDraftService;
import com.carenexus.ai.service.TrainingAiAssistanceService;
import com.carenexus.ai.vo.TrainingAiResponses;
import com.carenexus.common.response.ApiResponse;
import com.carenexus.common.response.PageResponse;
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
@RequestMapping("/api/v1/training/ai")
public class TrainingAiController {
    private final TrainingAiAssistanceService assistanceService;
    private final AiQuestionDraftService draftService;

    public TrainingAiController(TrainingAiAssistanceService assistanceService,
            AiQuestionDraftService draftService) {
        this.assistanceService = assistanceService;
        this.draftService = draftService;
    }

    @PostMapping("/qa")
    public ApiResponse<TrainingAiResponses.ContentResponse> answer(
            @Valid @RequestBody ContentRequest request) {
        return ApiResponse.success(assistanceService.answer(request));
    }

    @PostMapping("/summary")
    public ApiResponse<TrainingAiResponses.ContentResponse> summary(
            @Valid @RequestBody ContentRequest request) {
        return ApiResponse.success(assistanceService.summarize(request));
    }

    @PostMapping("/suggestions")
    public ApiResponse<TrainingAiResponses.ContentResponse> suggestions(
            @Valid @RequestBody ContentRequest request) {
        return ApiResponse.success(assistanceService.suggestions(request));
    }

    @PostMapping("/question-drafts")
    public ApiResponse<TrainingAiResponses.DraftBatchResponse> generateDrafts(
            @Valid @RequestBody QuestionDraftRequest request) {
        return ApiResponse.success(draftService.generate(request));
    }

    @GetMapping("/question-drafts")
    public ApiResponse<PageResponse<TrainingAiResponses.DraftResponse>> drafts(
            @RequestParam(required = false) String draftStatus,
            @RequestParam(defaultValue = "1") @Min(1) int pageNo,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize) {
        return ApiResponse.success(draftService.page(draftStatus, pageNo, pageSize));
    }

    @GetMapping("/question-drafts/{id}")
    public ApiResponse<TrainingAiResponses.DraftResponse> draft(@PathVariable Long id) {
        return ApiResponse.success(draftService.detail(id));
    }

    @PutMapping("/question-drafts/{id}/review")
    public ApiResponse<TrainingAiResponses.DraftResponse> review(@PathVariable Long id,
            @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.success(draftService.review(id, request));
    }
}
