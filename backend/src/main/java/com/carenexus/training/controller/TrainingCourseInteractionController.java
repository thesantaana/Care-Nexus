package com.carenexus.training.controller;

import com.carenexus.common.response.ApiResponse;
import com.carenexus.training.dto.AssignmentSubmissionRequest;
import com.carenexus.training.dto.DiscussionReplyRequest;
import com.carenexus.training.dto.DiscussionRequest;
import com.carenexus.training.service.TrainingCourseInteractionService;
import com.carenexus.training.vo.AssignmentResponse;
import com.carenexus.training.vo.DiscussionReplyResponse;
import com.carenexus.training.vo.DiscussionResponse;
import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@RestController
@ConditionalOnProperty(name = "care-nexus.course-interactions.enabled", havingValue = "true", matchIfMissing = true)
@RequestMapping("/api/v1/training")
public class TrainingCourseInteractionController {
    private final TrainingCourseInteractionService service;

    public TrainingCourseInteractionController(TrainingCourseInteractionService service) {
        this.service = service;
    }

    @GetMapping("/resources/{resourceId}/discussions")
    public ApiResponse<List<DiscussionResponse>> discussions(@PathVariable Long resourceId,
            @RequestParam(defaultValue = "LATEST") String sort) {
        return ApiResponse.success(service.discussions(resourceId, sort));
    }

    @PostMapping("/resources/{resourceId}/discussions")
    public ApiResponse<DiscussionResponse> createDiscussion(@PathVariable Long resourceId,
            @Valid @RequestBody DiscussionRequest request) {
        return ApiResponse.success(service.createDiscussion(resourceId, request));
    }

    @GetMapping("/discussions/{discussionId}/replies")
    public ApiResponse<List<DiscussionReplyResponse>> replies(@PathVariable Long discussionId) {
        return ApiResponse.success(service.replies(discussionId));
    }

    @PostMapping("/discussions/{discussionId}/replies")
    public ApiResponse<DiscussionReplyResponse> createReply(@PathVariable Long discussionId,
            @Valid @RequestBody DiscussionReplyRequest request) {
        return ApiResponse.success(service.createReply(discussionId, request));
    }

    @PutMapping("/discussions/{discussionId}/like")
    public ApiResponse<Boolean> toggleDiscussionLike(@PathVariable Long discussionId) {
        return ApiResponse.success(service.toggleDiscussionLike(discussionId));
    }

    @PutMapping("/discussion-replies/{replyId}/like")
    public ApiResponse<Boolean> toggleReplyLike(@PathVariable Long replyId) {
        return ApiResponse.success(service.toggleReplyLike(replyId));
    }

    @DeleteMapping("/discussions/{discussionId}")
    public ApiResponse<Void> deleteDiscussion(@PathVariable Long discussionId) {
        service.deleteDiscussion(discussionId);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/discussion-replies/{replyId}")
    public ApiResponse<Void> deleteReply(@PathVariable Long replyId) {
        service.deleteReply(replyId);
        return ApiResponse.success(null);
    }

    @GetMapping("/resources/{resourceId}/assignments")
    public ApiResponse<List<AssignmentResponse>> assignments(@PathVariable Long resourceId) {
        return ApiResponse.success(service.assignments(resourceId));
    }

    @PostMapping("/assignments/{assignmentId}/submission")
    public ApiResponse<AssignmentResponse> submitAssignment(@PathVariable Long assignmentId,
            @Valid @RequestBody AssignmentSubmissionRequest request) {
        return ApiResponse.success(service.submitAssignment(assignmentId, request));
    }
}
