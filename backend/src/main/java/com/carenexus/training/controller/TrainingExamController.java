package com.carenexus.training.controller;

import com.carenexus.common.response.ApiResponse;
import com.carenexus.training.dto.ExamQuestionsRequest;
import com.carenexus.training.dto.ExamRequest;
import com.carenexus.training.dto.LearningAccessRequest;
import com.carenexus.training.dto.QuestionOptionsRequest;
import com.carenexus.training.dto.QuestionRequest;
import com.carenexus.training.dto.SubmitExamRequest;
import com.carenexus.training.exam.service.TrainingExamManageService;
import com.carenexus.training.exam.service.TrainingExamTakingService;
import com.carenexus.training.exam.service.TrainingLearningService;
import com.carenexus.training.exam.service.TrainingQuestionService;
import com.carenexus.training.exam.service.TrainingScoreService;
import com.carenexus.training.vo.ExamRecordResponse;
import com.carenexus.training.vo.ExamResponse;
import com.carenexus.training.vo.LearningAccessResponse;
import com.carenexus.training.vo.LearningRecordResponse;
import com.carenexus.training.vo.QuestionResponse;
import com.carenexus.training.vo.TrainingScoreSummaryResponse;
import com.carenexus.training.vo.CaregiverTrainingScoreResponse;
import javax.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/training")
public class TrainingExamController {

    private final TrainingExamManageService examManageService;
    private final TrainingQuestionService questionService;
    private final TrainingLearningService learningService;
    private final TrainingExamTakingService examTakingService;
    private final TrainingScoreService scoreService;

    public TrainingExamController(TrainingExamManageService examManageService, TrainingQuestionService questionService,
            TrainingLearningService learningService, TrainingExamTakingService examTakingService,
            TrainingScoreService scoreService) {
        this.examManageService = examManageService;
        this.questionService = questionService;
        this.learningService = learningService;
        this.examTakingService = examTakingService;
        this.scoreService = scoreService;
    }

    @PostMapping("/exams")
    public ApiResponse<ExamResponse> createExam(@Valid @RequestBody ExamRequest request) {
        return ApiResponse.success(examManageService.createExam(request));
    }

    @PutMapping("/exams/{id}")
    public ApiResponse<ExamResponse> updateExam(@PathVariable Long id, @Valid @RequestBody ExamRequest request) {
        return ApiResponse.success(examManageService.updateExam(id, request));
    }

    @PutMapping("/exams/{id}/publish")
    public ApiResponse<ExamResponse> publishExam(@PathVariable Long id) {
        return ApiResponse.success(examManageService.publishExam(id));
    }

    @PostMapping("/questions")
    public ApiResponse<QuestionResponse> createQuestion(@Valid @RequestBody QuestionRequest request) {
        return ApiResponse.success(questionService.createQuestion(request));
    }

    @PutMapping("/questions/{id}")
    public ApiResponse<QuestionResponse> updateQuestion(@PathVariable Long id,
            @Valid @RequestBody QuestionRequest request) {
        return ApiResponse.success(questionService.updateQuestion(id, request));
    }

    @PutMapping("/questions/{id}/options")
    public ApiResponse<QuestionResponse> replaceQuestionOptions(@PathVariable Long id,
            @Valid @RequestBody QuestionOptionsRequest request) {
        return ApiResponse.success(questionService.replaceQuestionOptions(id, request));
    }

    @PutMapping("/exams/{id}/questions")
    public ApiResponse<ExamResponse> replaceExamQuestions(@PathVariable Long id,
            @Valid @RequestBody ExamQuestionsRequest request) {
        return ApiResponse.success(examManageService.replaceExamQuestions(id, request));
    }

    @PostMapping("/learning/access")
    public ApiResponse<LearningAccessResponse> recordLearningAccess(
            @Valid @RequestBody LearningAccessRequest request) {
        return ApiResponse.success(learningService.recordLearningAccess(request));
    }

    @GetMapping("/learning/me")
    public ApiResponse<LearningRecordResponse> myLearningRecord() {
        return ApiResponse.success(learningService.myLearningRecord());
    }

    @GetMapping("/exams")
    public ApiResponse<List<ExamResponse>> listExams() {
        return ApiResponse.success(examManageService.listExams());
    }

    @GetMapping("/learning/scores")
    public ApiResponse<TrainingScoreSummaryResponse> myCourseScores() {
        return ApiResponse.success(scoreService.myCourseScores());
    }

    @GetMapping("/learning/scores/users")
    public ApiResponse<List<CaregiverTrainingScoreResponse>> caregiverScores() {
        return ApiResponse.success(scoreService.caregiverScores());
    }

    @GetMapping("/exams/{examId}")
    public ApiResponse<ExamResponse> getPublishedExam(@PathVariable Long examId) {
        return ApiResponse.success(examTakingService.getPublishedExam(examId));
    }

    @PostMapping("/exams/{examId}/records")
    public ApiResponse<ExamRecordResponse> submitExam(@PathVariable Long examId,
            @Valid @RequestBody SubmitExamRequest request) {
        return ApiResponse.success(examTakingService.submitExam(examId, request));
    }
}
