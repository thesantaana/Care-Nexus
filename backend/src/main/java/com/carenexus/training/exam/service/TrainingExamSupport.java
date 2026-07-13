package com.carenexus.training.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.training.constant.ExamQuestionType;
import com.carenexus.training.constant.ExamStatus;
import com.carenexus.training.constant.TrainingResourceStatus;
import com.carenexus.training.entity.ExamQuestion;
import com.carenexus.training.entity.TrainingExam;
import com.carenexus.training.entity.TrainingResource;
import com.carenexus.training.mapper.ExamQuestionMapper;
import com.carenexus.training.mapper.TrainingExamMapper;
import com.carenexus.training.mapper.TrainingResourceMapper;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TrainingExamSupport {

    private static final int ACTIVE_FLAG = 0;

    private final TrainingExamMapper examMapper;
    private final ExamQuestionMapper questionMapper;
    private final TrainingResourceMapper resourceMapper;

    public TrainingExamSupport(TrainingExamMapper examMapper, ExamQuestionMapper questionMapper,
            TrainingResourceMapper resourceMapper) {
        this.examMapper = examMapper;
        this.questionMapper = questionMapper;
        this.resourceMapper = resourceMapper;
    }

    public TrainingExam requireExam(Long id) {
        TrainingExam exam = examMapper.selectById(id);
        if (exam == null || Integer.valueOf(1).equals(exam.getIsDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Training exam not found");
        }
        return exam;
    }

    public ExamQuestion requireQuestion(Long id) {
        ExamQuestion question = questionMapper.selectById(id);
        if (question == null || Integer.valueOf(1).equals(question.getIsDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Exam question not found");
        }
        return question;
    }

    public List<ExamQuestion> selectQuestions(List<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return questionMapper.selectBatchIds(ids);
    }

    public TrainingResource requirePublishedResource(Long id) {
        TrainingResource resource = resourceMapper.selectById(id);
        if (resource == null || Integer.valueOf(1).equals(resource.getIsDeleted())
                || !TrainingResourceStatus.PUBLISHED.equals(resource.getResourceStatus())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Training resource not found");
        }
        return resource;
    }

    public TrainingResource requireResource(Long id) {
        TrainingResource resource = resourceMapper.selectById(id);
        if (resource == null || Integer.valueOf(1).equals(resource.getIsDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Training resource not found");
        }
        return resource;
    }

    public TrainingExam requirePublishedExam(Long id) {
        TrainingExam exam = requireExam(id);
        if (!ExamStatus.PUBLISHED.equals(exam.getExamStatus())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Training exam not found");
        }
        return exam;
    }

    public void ensureUniqueExamName(String name, Long excludeId) {
        QueryWrapper<TrainingExam> wrapper = new QueryWrapper<TrainingExam>()
                .eq("exam_name", name)
                .eq("is_deleted", ACTIVE_FLAG);
        if (excludeId != null) {
            wrapper.ne("id", excludeId);
        }
        if (examMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "Training exam name already exists");
        }
    }

    public void ensureUniqueExamResource(Long resourceId, Long excludeId) {
        QueryWrapper<TrainingExam> wrapper = new QueryWrapper<TrainingExam>()
                .eq("resource_id", resourceId)
                .eq("is_deleted", ACTIVE_FLAG);
        if (excludeId != null) {
            wrapper.ne("id", excludeId);
        }
        if (examMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "Training resource already has an exam");
        }
    }

    public void validateQuestionType(String type) {
        if (!Arrays.asList(ExamQuestionType.SINGLE_CHOICE, ExamQuestionType.TRUE_FALSE).contains(type)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid question type");
        }
    }

    public void validatePublishedDraft(String status, String message) {
        if (!Arrays.asList(ExamStatus.DRAFT, ExamStatus.PUBLISHED).contains(status)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, message);
        }
    }

    public BigDecimal requireNonNegative(BigDecimal value, String message) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, message);
        }
        return value;
    }

    public Integer requirePositive(Integer value, String message) {
        if (value == null || value <= 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, message);
        }
        return value;
    }
}
