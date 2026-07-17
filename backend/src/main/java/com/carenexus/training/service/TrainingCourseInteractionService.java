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
import com.carenexus.training.entity.TrainingDiscussionLike;
import com.carenexus.training.entity.TrainingDiscussionReply;
import com.carenexus.training.entity.TrainingDiscussionReplyLike;
import com.carenexus.training.entity.TrainingResource;
import com.carenexus.training.mapper.TrainingAssignmentMapper;
import com.carenexus.training.mapper.TrainingAssignmentSubmissionMapper;
import com.carenexus.training.mapper.TrainingDiscussionMapper;
import com.carenexus.training.mapper.TrainingDiscussionLikeMapper;
import com.carenexus.training.mapper.TrainingDiscussionReplyMapper;
import com.carenexus.training.mapper.TrainingDiscussionReplyLikeMapper;
import com.carenexus.training.mapper.TrainingResourceMapper;
import com.carenexus.training.resource.support.TrainingResourceAccessPolicy;
import com.carenexus.training.vo.AssignmentResponse;
import com.carenexus.training.vo.DiscussionReplyResponse;
import com.carenexus.training.vo.DiscussionResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
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
    private final TrainingDiscussionLikeMapper discussionLikeMapper;
    private final TrainingDiscussionReplyMapper replyMapper;
    private final TrainingDiscussionReplyLikeMapper replyLikeMapper;
    private final TrainingAssignmentMapper assignmentMapper;
    private final TrainingAssignmentSubmissionMapper submissionMapper;
    private final SysUserMapper userMapper;

    public TrainingCourseInteractionService(CurrentUserService currentUserService,
            TrainingResourceAccessPolicy accessPolicy, TrainingResourceMapper resourceMapper,
            TrainingDiscussionMapper discussionMapper, TrainingDiscussionLikeMapper discussionLikeMapper,
            TrainingDiscussionReplyMapper replyMapper, TrainingDiscussionReplyLikeMapper replyLikeMapper,
            TrainingAssignmentMapper assignmentMapper, TrainingAssignmentSubmissionMapper submissionMapper,
            SysUserMapper userMapper) {
        this.currentUserService = currentUserService;
        this.accessPolicy = accessPolicy;
        this.resourceMapper = resourceMapper;
        this.discussionMapper = discussionMapper;
        this.discussionLikeMapper = discussionLikeMapper;
        this.replyMapper = replyMapper;
        this.replyLikeMapper = replyLikeMapper;
        this.assignmentMapper = assignmentMapper;
        this.submissionMapper = submissionMapper;
        this.userMapper = userMapper;
    }

    public List<DiscussionResponse> discussions(Long resourceId, String sort) {
        CurrentUser user = currentUser();
        requireResource(resourceId);
        List<DiscussionResponse> responses = new ArrayList<>();
        List<TrainingDiscussion> topics = discussionMapper.selectList(new QueryWrapper<TrainingDiscussion>()
                .eq("resource_id", resourceId).orderByDesc("created_at"));
        for (TrainingDiscussion topic : topics) {
            DiscussionResponse response = new DiscussionResponse();
            response.id = String.valueOf(topic.getId());
            response.resourceId = topic.getResourceId();
            response.title = topic.getTitle();
            response.content = topic.getContent();
            response.authorName = userName(topic.getUserId());
            response.replyCount = Math.toIntExact(replyMapper.selectCount(
                    new QueryWrapper<TrainingDiscussionReply>().eq("discussion_id", topic.getId())));
            response.likeCount = discussionLikeMapper.selectCount(
                    new QueryWrapper<TrainingDiscussionLike>().eq("discussion_id", topic.getId()));
            response.liked = discussionLikeMapper.selectCount(new QueryWrapper<TrainingDiscussionLike>()
                    .eq("discussion_id", topic.getId()).eq("user_id", user.getUserId())) > 0;
            response.ownedByCurrentUser = topic.getUserId().equals(user.getUserId());
            response.createdAt = topic.getCreatedAt();
            responses.add(response);
        }
        if ("HOT".equalsIgnoreCase(sort)) {
            responses.sort(Comparator.comparingLong((DiscussionResponse item) -> item.likeCount)
                    .thenComparingInt(item -> item.replyCount).reversed());
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
        return discussions(resourceId, "LATEST").stream()
                .filter(item -> item.id.equals(String.valueOf(topic.getId()))).findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Discussion not found"));
    }

    public List<DiscussionReplyResponse> replies(Long discussionId) {
        CurrentUser user = currentUser();
        requireDiscussion(discussionId);
        List<DiscussionReplyResponse> responses = new ArrayList<>();
        List<TrainingDiscussionReply> replies = replyMapper.selectList(
                new QueryWrapper<TrainingDiscussionReply>().eq("discussion_id", discussionId)
                        .orderByAsc("created_at"));
        for (TrainingDiscussionReply reply : replies) {
            DiscussionReplyResponse response = new DiscussionReplyResponse();
            response.id = String.valueOf(reply.getId());
            response.content = reply.getContent();
            response.authorName = userName(reply.getUserId());
            response.parentReplyId = reply.getParentReplyId() == null ? null : String.valueOf(reply.getParentReplyId());
            response.replyToAuthorName = reply.getParentReplyId() == null ? null
                    : userName(requireReply(reply.getParentReplyId()).getUserId());
            response.likeCount = replyLikeMapper.selectCount(
                    new QueryWrapper<TrainingDiscussionReplyLike>().eq("reply_id", reply.getId()));
            response.liked = replyLikeMapper.selectCount(new QueryWrapper<TrainingDiscussionReplyLike>()
                    .eq("reply_id", reply.getId()).eq("user_id", user.getUserId())) > 0;
            response.ownedByCurrentUser = reply.getUserId().equals(user.getUserId());
            response.createdAt = reply.getCreatedAt();
            responses.add(response);
        }
        return responses;
    }

    @Transactional
    public DiscussionReplyResponse createReply(Long discussionId, DiscussionReplyRequest request) {
        CurrentUser user = currentUser();
        requireDiscussion(discussionId);
        Long parentReplyId = request.getParentReplyId();
        if (parentReplyId != null) {
            TrainingDiscussionReply parent = requireReply(parentReplyId);
            if (!discussionId.equals(parent.getDiscussionId())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "Reply target does not belong to this discussion");
            }
            if (parent.getParentReplyId() != null) {
                parentReplyId = parent.getParentReplyId();
            }
        }
        TrainingDiscussionReply reply = new TrainingDiscussionReply();
        reply.setDiscussionId(discussionId);
        reply.setParentReplyId(parentReplyId);
        reply.setUserId(user.getUserId());
        reply.setContent(request.getContent().trim());
        reply.setCreatedAt(LocalDateTime.now());
        replyMapper.insert(reply);
        return replies(discussionId).stream().filter(item -> item.id.equals(String.valueOf(reply.getId()))).findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Discussion reply not found"));
    }

    @Transactional
    public boolean toggleDiscussionLike(Long discussionId) {
        CurrentUser user = currentUser();
        requireDiscussion(discussionId);
        QueryWrapper<TrainingDiscussionLike> query = new QueryWrapper<TrainingDiscussionLike>()
                .eq("discussion_id", discussionId).eq("user_id", user.getUserId());
        TrainingDiscussionLike existing = discussionLikeMapper.selectOne(query);
        if (existing != null) {
            discussionLikeMapper.deleteById(existing.getId());
            return false;
        }
        TrainingDiscussionLike like = new TrainingDiscussionLike();
        like.setDiscussionId(discussionId);
        like.setUserId(user.getUserId());
        like.setCreatedAt(LocalDateTime.now());
        discussionLikeMapper.insert(like);
        return true;
    }

    @Transactional
    public boolean toggleReplyLike(Long replyId) {
        CurrentUser user = currentUser();
        requireReply(replyId);
        QueryWrapper<TrainingDiscussionReplyLike> query = new QueryWrapper<TrainingDiscussionReplyLike>()
                .eq("reply_id", replyId).eq("user_id", user.getUserId());
        TrainingDiscussionReplyLike existing = replyLikeMapper.selectOne(query);
        if (existing != null) {
            replyLikeMapper.deleteById(existing.getId());
            return false;
        }
        TrainingDiscussionReplyLike like = new TrainingDiscussionReplyLike();
        like.setReplyId(replyId);
        like.setUserId(user.getUserId());
        like.setCreatedAt(LocalDateTime.now());
        replyLikeMapper.insert(like);
        return true;
    }

    @Transactional
    public void deleteDiscussion(Long discussionId) {
        CurrentUser user = currentUser();
        TrainingDiscussion discussion = requireDiscussion(discussionId);
        if (!discussion.getUserId().equals(user.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Only the author can delete this discussion");
        }
        replyMapper.delete(new QueryWrapper<TrainingDiscussionReply>().eq("discussion_id", discussionId));
        discussionMapper.deleteById(discussionId);
    }

    @Transactional
    public void deleteReply(Long replyId) {
        CurrentUser user = currentUser();
        TrainingDiscussionReply reply = requireReply(replyId);
        if (!reply.getUserId().equals(user.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Only the author can delete this reply");
        }
        replyMapper.deleteById(replyId);
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
        response.answerAnalysis = assignment.getAnswerAnalysis();
        response.dueAt = assignment.getDueAt();
        if (submission != null) {
            response.submissionStatus = submission.getSubmissionStatus();
            response.submittedAnswer = submission.getAnswerContent();
            response.score = submission.getScore();
            if ("GRADED".equals(submission.getSubmissionStatus())) {
                response.correctAnswer = assignment.getStandardAnswer();
            }
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

    private TrainingDiscussionReply requireReply(Long replyId) {
        TrainingDiscussionReply reply = replyMapper.selectById(replyId);
        if (reply == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Discussion reply not found");
        }
        return reply;
    }

    private String userName(Long userId) {
        SysUser user = userMapper.selectById(userId);
        return user == null ? "已注销用户" : user.getRealName();
    }
}
