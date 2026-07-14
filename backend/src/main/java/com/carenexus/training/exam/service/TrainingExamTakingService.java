package com.carenexus.training.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.carenexus.audit.OperationLogService;
import com.carenexus.auth.CurrentUser;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.training.constant.ExamPassStatus;
import com.carenexus.training.constant.ExamQuestionType;
import com.carenexus.training.constant.LearningStatus;
import com.carenexus.training.dto.ExamAnswerRequest;
import com.carenexus.training.dto.SubmitExamRequest;
import com.carenexus.training.entity.ExamAnswer;
import com.carenexus.training.entity.ExamQuestion;
import com.carenexus.training.entity.ExamRecord;
import com.carenexus.training.entity.LearningRecord;
import com.carenexus.training.entity.TrainingExam;
import com.carenexus.training.entity.TrainingExamQuestion;
import com.carenexus.training.mapper.ExamAnswerMapper;
import com.carenexus.training.mapper.ExamRecordMapper;
import com.carenexus.training.mapper.LearningRecordMapper;
import com.carenexus.training.mapper.TrainingExamQuestionMapper;
import com.carenexus.training.resource.support.TrainingResourceAccessPolicy;
import com.carenexus.training.support.TrainingText;
import com.carenexus.training.vo.ExamRecordResponse;
import com.carenexus.training.vo.ExamResponse;
import com.carenexus.training.vo.MistakeQuestionResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class TrainingExamTakingService {

    private static final String LOG_RESULT_SUCCESS = "SUCCESS";

    private final TrainingResourceAccessPolicy accessPolicy;
    private final OperationLogService operationLogService;
    private final TrainingExamSupport support;
    private final TrainingExamAssembler assembler;
    private final TrainingLearningService learningService;
    private final TrainingExamQuestionMapper examQuestionMapper;
    private final ExamRecordMapper examRecordMapper;
    private final ExamAnswerMapper examAnswerMapper;
    private final LearningRecordMapper learningRecordMapper;

    public TrainingExamTakingService(TrainingResourceAccessPolicy accessPolicy,
            OperationLogService operationLogService, TrainingExamSupport support, TrainingExamAssembler assembler,
            TrainingLearningService learningService, TrainingExamQuestionMapper examQuestionMapper,
            ExamRecordMapper examRecordMapper, ExamAnswerMapper examAnswerMapper,
            LearningRecordMapper learningRecordMapper) {
        this.accessPolicy = accessPolicy;
        this.operationLogService = operationLogService;
        this.support = support;
        this.assembler = assembler;
        this.learningService = learningService;
        this.examQuestionMapper = examQuestionMapper;
        this.examRecordMapper = examRecordMapper;
        this.examAnswerMapper = examAnswerMapper;
        this.learningRecordMapper = learningRecordMapper;
    }

    public ExamResponse getPublishedExam(Long examId) {
        accessPolicy.requireViewOrManage();
        return assembler.toExamResponse(support.requirePublishedExam(examId), false);
    }

    public List<MistakeQuestionResponse> myMistakes(Long resourceId) {
        CurrentUser currentUser = accessPolicy.requireViewOrManage();
        return examAnswerMapper.selectLatestMistakes(currentUser.getUserId(), resourceId);
    }

    @Transactional
    public ExamRecordResponse submitExam(Long examId, SubmitExamRequest request) {
        CurrentUser currentUser = accessPolicy.requireViewOrManage();
        TrainingExam exam = support.requirePublishedExam(examId);
        LearningRecord learningRecord = learningService.getOrCreateLearningRecord(currentUser.getUserId());
        if (!learningService.isCourseCompleted(currentUser.getUserId(), exam.getResourceId())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Learning requirement is not met");
        }
        int nextAttempt = examRecordMapper.selectMaxAttemptNo(currentUser.getUserId(), examId) + 1;
        if (nextAttempt > exam.getMaxAttempts()) {
            throw new BusinessException(ErrorCode.CONFLICT, "Exam max attempts exceeded");
        }
        ExamRecord record = createExamRecord(examId, currentUser.getUserId(), nextAttempt);
        BigDecimal totalScore = scoreAnswers(record.getId(), examId, normalizeAnswers(request.getAnswers()));
        String passStatus = totalScore.compareTo(exam.getPassScore()) >= 0
                ? ExamPassStatus.PASSED : ExamPassStatus.NOT_PASSED;
        record.setScore(totalScore);
        record.setPassStatus(passStatus);
        examRecordMapper.updateById(record);
        learningRecord.setTrainingStatus(resolveTrainingStatusAfterExam(currentUser.getUserId(), examId, passStatus));
        learningRecordMapper.updateById(learningRecord);
        operationLogService.record(currentUser, "TRAINING_EXAM_SUBMIT", "TRAINING_EXAM", exam.getId(),
                LOG_RESULT_SUCCESS);
        return assembler.toExamRecordResponse(record, learningRecord.getTrainingStatus());
    }

    private ExamRecord createExamRecord(Long examId, Long userId, int attemptNo) {
        ExamRecord record = new ExamRecord();
        record.setExamId(examId);
        record.setUserId(userId);
        record.setAttemptNo(attemptNo);
        record.setSubmittedAt(LocalDateTime.now());
        record.setPassStatus(ExamPassStatus.NOT_PASSED);
        examRecordMapper.insert(record);
        return record;
    }

    private BigDecimal scoreAnswers(Long recordId, Long examId, Map<Long, String> answers) {
        List<TrainingExamQuestion> examQuestions = examQuestionMapper.selectByExamId(examId);
        if (examQuestions.isEmpty()) {
            throw new BusinessException(ErrorCode.CONFLICT, "Training exam has no question");
        }
        BigDecimal totalScore = BigDecimal.ZERO;
        for (TrainingExamQuestion examQuestion : examQuestions) {
            String answer = answers.get(examQuestion.getQuestionId());
            if (!StringUtils.hasText(answer)) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "All exam questions must be answered");
            }
            ExamQuestion question = support.requireQuestion(examQuestion.getQuestionId());
            BigDecimal answerScore = answerMatches(question, answer) ? examQuestion.getScore() : BigDecimal.ZERO;
            totalScore = totalScore.add(answerScore);
            insertAnswer(recordId, question.getId(), answer, answerScore);
        }
        return totalScore;
    }

    private String resolveTrainingStatusAfterExam(Long userId, Long examId, String currentPassStatus) {
        if (ExamPassStatus.PASSED.equals(currentPassStatus)) {
            return LearningStatus.PASSED;
        }
        QueryWrapper<ExamRecord> wrapper = new QueryWrapper<ExamRecord>()
                .eq("user_id", userId)
                .eq("exam_id", examId)
                .eq("pass_status", ExamPassStatus.PASSED);
        return examRecordMapper.selectCount(wrapper) > 0 ? LearningStatus.PASSED : LearningStatus.EXAM_TAKEN;
    }

    private void insertAnswer(Long recordId, Long questionId, String answer, BigDecimal score) {
        ExamAnswer examAnswer = new ExamAnswer();
        examAnswer.setExamRecordId(recordId);
        examAnswer.setQuestionId(questionId);
        examAnswer.setUserAnswer(answer);
        examAnswer.setScore(score);
        examAnswerMapper.insert(examAnswer);
    }

    private boolean answerMatches(ExamQuestion question, String answer) {
        String normalizedAnswer = TrainingText.required(answer, "Answer is required");
        String standard = TrainingText.required(question.getStandardAnswer(), "Question standard answer is missing");
        if (ExamQuestionType.TRUE_FALSE.equals(question.getQuestionType())) {
            return normalizeBoolean(normalizedAnswer).equals(normalizeBoolean(standard));
        }
        return normalizedAnswer.equalsIgnoreCase(standard);
    }

    private Map<Long, String> normalizeAnswers(List<ExamAnswerRequest> answers) {
        Map<Long, String> result = new LinkedHashMap<>();
        for (ExamAnswerRequest answer : answers) {
            if (result.put(answer.getQuestionId(), TrainingText.required(answer.getAnswer(), "Answer is required"))
                    != null) {
                throw new BusinessException(ErrorCode.CONFLICT, "Duplicate answer question");
            }
        }
        return result;
    }

    private String normalizeBoolean(String value) {
        String normalized = TrainingText.required(value, "Boolean answer is required").toLowerCase();
        if ("true".equals(normalized) || "正确".equals(normalized)) {
            return "true";
        }
        if ("false".equals(normalized) || "错误".equals(normalized)) {
            return "false";
        }
        throw new BusinessException(ErrorCode.BAD_REQUEST, "Boolean answer must be true or false");
    }
}
