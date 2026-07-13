package com.carenexus.training.exam.service;

import com.carenexus.audit.OperationLogService;
import com.carenexus.auth.CurrentUser;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.training.constant.ExamStatus;
import com.carenexus.training.dto.ExamQuestionRequest;
import com.carenexus.training.dto.ExamQuestionsRequest;
import com.carenexus.training.dto.ExamRequest;
import com.carenexus.training.entity.ExamQuestion;
import com.carenexus.training.entity.TrainingExam;
import com.carenexus.training.entity.TrainingExamQuestion;
import com.carenexus.training.mapper.TrainingExamMapper;
import com.carenexus.training.mapper.TrainingExamQuestionMapper;
import com.carenexus.training.resource.support.TrainingResourceAccessPolicy;
import com.carenexus.training.support.TrainingText;
import com.carenexus.training.vo.ExamResponse;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainingExamManageService {

    private static final int ACTIVE_FLAG = 0;
    private static final String LOG_RESULT_SUCCESS = "SUCCESS";

    private final TrainingResourceAccessPolicy accessPolicy;
    private final OperationLogService operationLogService;
    private final TrainingExamSupport support;
    private final TrainingExamAssembler assembler;
    private final TrainingExamMapper examMapper;
    private final TrainingExamQuestionMapper examQuestionMapper;

    public TrainingExamManageService(TrainingResourceAccessPolicy accessPolicy,
            OperationLogService operationLogService, TrainingExamSupport support, TrainingExamAssembler assembler,
            TrainingExamMapper examMapper, TrainingExamQuestionMapper examQuestionMapper) {
        this.accessPolicy = accessPolicy;
        this.operationLogService = operationLogService;
        this.support = support;
        this.assembler = assembler;
        this.examMapper = examMapper;
        this.examQuestionMapper = examQuestionMapper;
    }

    public ExamResponse createExam(ExamRequest request) {
        accessPolicy.requireManage();
        String name = TrainingText.required(request.getExamName(), "Exam name is required");
        support.ensureUniqueExamName(name, null);
        support.requireResource(request.getResourceId());
        support.ensureUniqueExamResource(request.getResourceId(), null);
        TrainingExam exam = new TrainingExam();
        exam.setResourceId(request.getResourceId());
        exam.setExamName(name);
        exam.setPassScore(support.requireNonNegative(request.getPassScore(), "Pass score is required"));
        exam.setMaxAttempts(support.requirePositive(request.getMaxAttempts(), "Max attempts must be greater than 0"));
        exam.setExamStatus(ExamStatus.DRAFT);
        exam.setIsDeleted(ACTIVE_FLAG);
        examMapper.insert(exam);
        return assembler.toExamResponse(exam, true);
    }

    public ExamResponse updateExam(Long id, ExamRequest request) {
        accessPolicy.requireManage();
        TrainingExam exam = support.requireExam(id);
        if (ExamStatus.PUBLISHED.equals(exam.getExamStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Published exam cannot be updated");
        }
        String name = TrainingText.required(request.getExamName(), "Exam name is required");
        support.ensureUniqueExamName(name, id);
        support.requireResource(request.getResourceId());
        support.ensureUniqueExamResource(request.getResourceId(), id);
        exam.setResourceId(request.getResourceId());
        exam.setExamName(name);
        exam.setPassScore(support.requireNonNegative(request.getPassScore(), "Pass score is required"));
        exam.setMaxAttempts(support.requirePositive(request.getMaxAttempts(), "Max attempts must be greater than 0"));
        examMapper.updateById(exam);
        return assembler.toExamResponse(exam, true);
    }

    public ExamResponse publishExam(Long id) {
        CurrentUser currentUser = accessPolicy.requireManage();
        TrainingExam exam = support.requireExam(id);
        if (ExamStatus.PUBLISHED.equals(exam.getExamStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Training exam is already published");
        }
        if (examQuestionMapper.selectByExamId(id).isEmpty()) {
            throw new BusinessException(ErrorCode.CONFLICT, "Training exam must contain questions before publish");
        }
        exam.setExamStatus(ExamStatus.PUBLISHED);
        examMapper.updateById(exam);
        operationLogService.record(currentUser, "TRAINING_EXAM_PUBLISH", "TRAINING_EXAM", exam.getId(),
                LOG_RESULT_SUCCESS);
        return assembler.toExamResponse(exam, true);
    }

    @Transactional
    public ExamResponse replaceExamQuestions(Long id, ExamQuestionsRequest request) {
        accessPolicy.requireManage();
        TrainingExam exam = support.requireExam(id);
        if (ExamStatus.PUBLISHED.equals(exam.getExamStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Published exam questions cannot be changed");
        }
        Set<Long> questionIds = new LinkedHashSet<>();
        examQuestionMapper.deleteByExamId(id);
        int sortNo = 1;
        for (ExamQuestionRequest questionRequest : request.getQuestions()) {
            if (!questionIds.add(questionRequest.getQuestionId())) {
                throw new BusinessException(ErrorCode.CONFLICT, "Duplicate question in exam");
            }
            insertExamQuestion(id, questionRequest, sortNo);
            sortNo++;
        }
        return assembler.toExamResponse(exam, true);
    }

    private void insertExamQuestion(Long examId, ExamQuestionRequest request, int defaultSortNo) {
        ExamQuestion question = support.requireQuestion(request.getQuestionId());
        if (!ExamStatus.PUBLISHED.equals(question.getQuestionStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Only published question can be used in exam");
        }
        TrainingExamQuestion relation = new TrainingExamQuestion();
        relation.setExamId(examId);
        relation.setQuestionId(question.getId());
        relation.setScore(support.requireNonNegative(request.getScore(), "Question score is required"));
        relation.setSortNo(request.getSortNo() == null ? defaultSortNo : request.getSortNo());
        examQuestionMapper.insert(relation);
    }
}
