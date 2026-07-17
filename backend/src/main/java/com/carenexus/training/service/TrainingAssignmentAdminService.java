package com.carenexus.training.service;

import com.carenexus.auth.CurrentUser;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.training.dto.AssignmentImportQuestion;
import com.carenexus.training.dto.AssignmentPublishRequest;
import com.carenexus.training.entity.TrainingAssignment;
import com.carenexus.training.entity.TrainingResource;
import com.carenexus.training.mapper.TrainingAssignmentMapper;
import com.carenexus.training.mapper.TrainingResourceMapper;
import com.carenexus.training.resource.support.TrainingResourceAccessPolicy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(name = "care-nexus.course-interactions.enabled", havingValue = "true", matchIfMissing = true)
public class TrainingAssignmentAdminService {
    private final TrainingResourceAccessPolicy accessPolicy;
    private final TrainingResourceMapper resourceMapper;
    private final TrainingAssignmentMapper assignmentMapper;
    private final ObjectMapper objectMapper;

    public TrainingAssignmentAdminService(TrainingResourceAccessPolicy accessPolicy,
            TrainingResourceMapper resourceMapper, TrainingAssignmentMapper assignmentMapper,
            ObjectMapper objectMapper) {
        this.accessPolicy = accessPolicy;
        this.resourceMapper = resourceMapper;
        this.assignmentMapper = assignmentMapper;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public int publish(AssignmentPublishRequest request) {
        CurrentUser user = accessPolicy.requireManage();
        TrainingResource resource = resourceMapper.selectById(request.resourceId);
        if (resource == null || Integer.valueOf(1).equals(resource.getIsDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "培训课程不存在");
        }
        int index = 1;
        for (AssignmentImportQuestion question : request.questions) {
            validateQuestion(question);
            TrainingAssignment assignment = new TrainingAssignment();
            assignment.setResourceId(request.resourceId);
            assignment.setTitle(request.title.trim() + "（第 " + index + " 题）");
            assignment.setAssignmentType(question.type);
            assignment.setContent(question.content.trim());
            assignment.setOptionsJson(toJson(question.options));
            assignment.setStandardAnswer(question.standardAnswer.trim());
            assignment.setAnswerAnalysis(question.analysis == null ? "" : question.analysis.trim());
            assignment.setAssignmentStatus("PUBLISHED");
            assignment.setCreatedBy(user.getUserId());
            assignment.setCreatedAt(LocalDateTime.now());
            assignmentMapper.insert(assignment);
            index++;
        }
        return request.questions.size();
    }

    private void validateQuestion(AssignmentImportQuestion question) {
        if (question == null || question.content == null || question.content.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "题干不能为空");
        }
        if (!Arrays.asList("SINGLE_CHOICE", "TRUE_FALSE").contains(question.type)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "仅支持单选题和判断题");
        }
        if (question.options == null || question.options.size() < 2 || question.standardAnswer == null
                || !question.options.contains(question.standardAnswer)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "选项和标准答案不完整");
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException error) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "题目选项格式无效");
        }
    }
}
