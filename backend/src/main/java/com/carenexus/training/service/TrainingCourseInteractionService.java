package com.carenexus.training.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.carenexus.auth.CurrentUser;
import com.carenexus.auth.CurrentUserService;
import com.carenexus.auth.entity.SysUser;
import com.carenexus.auth.mapper.SysUserMapper;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.training.dto.AssignmentSubmissionRequest;
import com.carenexus.training.dto.DiscussionReplyRequest;
import com.carenexus.training.dto.DiscussionRequest;
import com.carenexus.training.entity.TrainingAssignment;
import com.carenexus.training.entity.TrainingAssignmentSubmission;
import com.carenexus.training.entity.TrainingDiscussion;
import com.carenexus.training.entity.TrainingDiscussionReply;
import com.carenexus.training.entity.TrainingResource;
import com.carenexus.training.mapper.TrainingAssignmentMapper;
import com.carenexus.training.mapper.TrainingAssignmentSubmissionMapper;
import com.carenexus.training.mapper.TrainingDiscussionMapper;
import com.carenexus.training.mapper.TrainingDiscussionReplyMapper;
import com.carenexus.training.mapper.TrainingResourceMapper;
import com.carenexus.training.resource.support.TrainingResourceAccessPolicy;
import com.carenexus.training.vo.AssignmentResponse;
import com.carenexus.training.vo.DiscussionReplyResponse;
import com.carenexus.training.vo.DiscussionResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(name = "care-nexus.course-interactions.enabled", havingValue = "true", matchIfMissing = true)
public class TrainingCourseInteractionService {
    private final CurrentUserService currentUserService;
    private final TrainingResourceAccessPolicy accessPolicy;
    private final TrainingResourceMapper resourceMapper;
    private final TrainingDiscussionMapper discussionMapper;
    private final TrainingDiscussionReplyMapper replyMapper;
    private final TrainingAssignmentMapper assignmentMapper;
    private final TrainingAssignmentSubmissionMapper submissionMapper;
    private final SysUserMapper userMapper;

    public TrainingCourseInteractionService(CurrentUserService currentUserService,
            TrainingResourceAccessPolicy accessPolicy, TrainingResourceMapper resourceMapper,
            TrainingDiscussionMapper discussionMapper, TrainingDiscussionReplyMapper replyMapper,
            TrainingAssignmentMapper assignmentMapper, TrainingAssignmentSubmissionMapper submissionMapper,
            SysUserMapper userMapper) {
        this.currentUserService = currentUserService;
        this.accessPolicy = accessPolicy;
        this.resourceMapper = resourceMapper;
        this.discussionMapper = discussionMapper;
        this.replyMapper = replyMapper;
        this.assignmentMapper = assignmentMapper;
        this.submissionMapper = submissionMapper;
        this.userMapper = userMapper;
    }

    public List<DiscussionResponse> discussions(Long resourceId) {
        currentUser();
        requireResource(resourceId);
        List<DiscussionResponse> responses = new ArrayList<>();
        List<TrainingDiscussion> topics = discussionMapper.selectList(new QueryWrapper<TrainingDiscussion>()
                .eq("resource_id", resourceId).orderByDesc("created_at"));
        for (TrainingDiscussion topic : topics) {
            DiscussionResponse response = new DiscussionResponse();
            response.id = topic.getId();
            response.resourceId = topic.getResourceId();
            response.title = topic.getTitle();
            response.content = topic.getContent();
            response.authorName = userName(topic.getUserId());
            response.replyCount = Math.toIntExact(replyMapper.selectCount(
                    new QueryWrapper<TrainingDiscussionReply>().eq("discussion_id", topic.getId())));
            response.createdAt = topic.getCreatedAt();
            responses.add(response);
        }
        return responses;
    }

    @Transactional
    public DiscussionResponse createDiscussion(Long resourceId, DiscussionRequest request) {
        CurrentUser user = currentUser();
        requireResource(resourceId);
        TrainingDiscussion topic = new TrainingDiscussion();
        topic.setResourceId(resourceId);
        topic.setUserId(user.getUserId());
        topic.setTitle(request.getTitle().trim());
        topic.setContent(request.getContent().trim());
        topic.setCreatedAt(LocalDateTime.now());
        discussionMapper.insert(topic);
        return discussions(resourceId).stream().filter(item -> item.id.equals(topic.getId())).findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Discussion not found"));
    }

    public List<DiscussionReplyResponse> replies(Long discussionId) {
        currentUser();
        requireDiscussion(discussionId);
        List<DiscussionReplyResponse> responses = new ArrayList<>();
        List<TrainingDiscussionReply> replies = replyMapper.selectList(
                new QueryWrapper<TrainingDiscussionReply>().eq("discussion_id", discussionId)
                        .orderByAsc("created_at"));
        for (TrainingDiscussionReply reply : replies) {
            DiscussionReplyResponse response = new DiscussionReplyResponse();
            response.id = reply.getId();
            response.content = reply.getContent();
            response.authorName = userName(reply.getUserId());
            response.createdAt = reply.getCreatedAt();
            responses.add(response);
        }
        return responses;
    }

    @Transactional
    public DiscussionReplyResponse createReply(Long discussionId, DiscussionReplyRequest request) {
        CurrentUser user = currentUser();
        requireDiscussion(discussionId);
        TrainingDiscussionReply reply = new TrainingDiscussionReply();
        reply.setDiscussionId(discussionId);
        reply.setUserId(user.getUserId());
        reply.setContent(request.getContent().trim());
        reply.setCreatedAt(LocalDateTime.now());
        replyMapper.insert(reply);
        return replies(discussionId).stream().filter(item -> item.id.equals(reply.getId())).findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Discussion reply not found"));
    }

    public List<AssignmentResponse> assignments(Long resourceId) {
        CurrentUser user = currentUser();
        requireResource(resourceId);
        List<AssignmentResponse> responses = new ArrayList<>();
        List<TrainingAssignment> assignments = assignmentMapper.selectList(new QueryWrapper<TrainingAssignment>()
                .eq("resource_id", resourceId).eq("assignment_status", "PUBLISHED").orderByAsc("created_at"));
        for (TrainingAssignment assignment : assignments) {
            TrainingAssignmentSubmission submission = submissionMapper.selectOne(
                    new QueryWrapper<TrainingAssignmentSubmission>().eq("assignment_id", assignment.getId())
                            .eq("user_id", user.getUserId()));
            responses.add(toAssignmentResponse(assignment, submission));
        }
        return responses;
    }

    @Transactional
    public AssignmentResponse submitAssignment(Long assignmentId, AssignmentSubmissionRequest request) {
        CurrentUser user = currentUser();
        TrainingAssignment assignment = assignmentMapper.selectById(assignmentId);
        if (assignment == null || !"PUBLISHED".equals(assignment.getAssignmentStatus())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Assignment not found");
        }
        TrainingAssignmentSubmission submission = submissionMapper.selectOne(
                new QueryWrapper<TrainingAssignmentSubmission>().eq("assignment_id", assignmentId)
                        .eq("user_id", user.getUserId()));
        if (submission == null) {
            submission = new TrainingAssignmentSubmission();
            submission.setAssignmentId(assignmentId);
            submission.setUserId(user.getUserId());
        }
        String answer = request.getAnswer().trim();
        submission.setAnswerContent(answer);
        if ("TEXT".equals(assignment.getAssignmentType())) {
            submission.setScore(null);
            submission.setSubmissionStatus("SUBMITTED");
        } else {
            boolean correct = answersMatch(assignment, answer);
            submission.setScore(correct ? BigDecimal.valueOf(100) : BigDecimal.ZERO);
            submission.setSubmissionStatus("GRADED");
        }
        submission.setSubmittedAt(LocalDateTime.now());
        if (submission.getId() == null) {
            submissionMapper.insert(submission);
        } else {
            submissionMapper.updateById(submission);
        }
        return toAssignmentResponse(assignment, submission);
    }

    private boolean answersMatch(TrainingAssignment assignment, String answer) {
        String standardAnswer = assignment.getStandardAnswer();
        if ("TRUE_FALSE".equals(assignment.getAssignmentType())) {
            return normalizeBoolean(answer).equals(normalizeBoolean(standardAnswer));
        }
        return answer.equalsIgnoreCase(standardAnswer);
    }

    private String normalizeBoolean(String answer) {
        String normalized = answer == null ? "" : answer.trim().toLowerCase();
        if ("true".equals(normalized) || "正确".equals(normalized)) {
            return "true";
        }
        if ("false".equals(normalized) || "错误".equals(normalized)) {
            return "false";
        }
        throw new BusinessException(ErrorCode.BAD_REQUEST, "Boolean answer must be true or false");
    }

    private AssignmentResponse toAssignmentResponse(TrainingAssignment assignment,
            TrainingAssignmentSubmission submission) {
        AssignmentResponse response = new AssignmentResponse();
        response.id = assignment.getId();
        response.resourceId = assignment.getResourceId();
        response.title = assignment.getTitle();
        response.type = assignment.getAssignmentType();
        response.content = assignment.getContent();
        response.optionsJson = assignment.getOptionsJson();
        response.dueAt = assignment.getDueAt();
        if (submission != null) {
            response.submissionStatus = submission.getSubmissionStatus();
            response.submittedAnswer = submission.getAnswerContent();
            response.score = submission.getScore();
        }
        return response;
    }

    private CurrentUser currentUser() {
        accessPolicy.requireViewOrManage();
        return currentUserService.getCurrentUser()
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "Authentication required"));
    }

    private void requireResource(Long resourceId) {
        TrainingResource resource = resourceMapper.selectById(resourceId);
        if (resource == null || Integer.valueOf(1).equals(resource.getIsDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Training resource not found");
        }
    }

    private TrainingDiscussion requireDiscussion(Long discussionId) {
        TrainingDiscussion topic = discussionMapper.selectById(discussionId);
        if (topic == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Discussion not found");
        }
        return topic;
    }

    private String userName(Long userId) {
        SysUser user = userMapper.selectById(userId);
        return user == null ? "已注销用户" : user.getRealName();
    }
}
