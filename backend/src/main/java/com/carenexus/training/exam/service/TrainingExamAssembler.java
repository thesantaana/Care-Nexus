package com.carenexus.training.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.carenexus.training.entity.ExamQuestion;
import com.carenexus.training.entity.ExamQuestionOption;
import com.carenexus.training.entity.ExamRecord;
import com.carenexus.training.entity.TrainingExam;
import com.carenexus.training.entity.TrainingExamQuestion;
import com.carenexus.training.mapper.ExamQuestionOptionMapper;
import com.carenexus.training.mapper.TrainingExamQuestionMapper;
import com.carenexus.training.vo.ExamQuestionResponse;
import com.carenexus.training.vo.ExamRecordResponse;
import com.carenexus.training.vo.ExamResponse;
import com.carenexus.training.vo.QuestionOptionResponse;
import com.carenexus.training.vo.QuestionResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TrainingExamAssembler {

    private final TrainingExamSupport support;
    private final TrainingExamQuestionMapper examQuestionMapper;
    private final ExamQuestionOptionMapper optionMapper;

    public TrainingExamAssembler(TrainingExamSupport support, TrainingExamQuestionMapper examQuestionMapper,
            ExamQuestionOptionMapper optionMapper) {
        this.support = support;
        this.examQuestionMapper = examQuestionMapper;
        this.optionMapper = optionMapper;
    }

    public ExamResponse toExamResponse(TrainingExam exam, boolean includeAnswers) {
        ExamResponse response = new ExamResponse();
        response.setId(exam.getId());
        response.setResourceId(exam.getResourceId());
        response.setExamName(exam.getExamName());
        response.setPassScore(exam.getPassScore());
        response.setMaxAttempts(exam.getMaxAttempts());
        response.setStatus(exam.getExamStatus());
        List<TrainingExamQuestion> relations = examQuestionMapper.selectByExamId(exam.getId());
        List<ExamQuestionResponse> questions = new ArrayList<>();
        Map<Long, ExamQuestion> questionMap = loadQuestions(relations);
        Map<Long, List<ExamQuestionOption>> optionMap = loadOptions(questionMap.keySet().stream()
                .collect(Collectors.toList()));
        for (TrainingExamQuestion relation : relations) {
            ExamQuestion question = questionMap.get(relation.getQuestionId());
            if (question == null) {
                continue;
            }
            ExamQuestionResponse questionResponse = new ExamQuestionResponse();
            questionResponse.setQuestionId(relation.getQuestionId());
            questionResponse.setScore(relation.getScore());
            questionResponse.setSortNo(relation.getSortNo());
            questionResponse.setQuestion(toQuestionResponse(question,
                    optionMap.getOrDefault(question.getId(), Collections.emptyList()),
                    includeAnswers));
            questions.add(questionResponse);
        }
        response.setQuestions(questions);
        return response;
    }

    public QuestionResponse toQuestionResponse(ExamQuestion question, boolean includeAnswers) {
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setResourceId(question.getResourceId());
        response.setQuestionType(question.getQuestionType());
        response.setQuestionContent(question.getQuestionContent());
        response.setStandardAnswer(includeAnswers ? question.getStandardAnswer() : null);
        response.setAnalysis(includeAnswers ? question.getAnalysis() : null);
        response.setStatus(question.getQuestionStatus());
        response.setOptions(toOptionResponses(question.getId(), includeAnswers));
        return response;
    }

    private QuestionResponse toQuestionResponse(ExamQuestion question, List<ExamQuestionOption> options,
            boolean includeAnswers) {
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setResourceId(question.getResourceId());
        response.setQuestionType(question.getQuestionType());
        response.setQuestionContent(question.getQuestionContent());
        response.setStandardAnswer(includeAnswers ? question.getStandardAnswer() : null);
        response.setAnalysis(includeAnswers ? question.getAnalysis() : null);
        response.setStatus(question.getQuestionStatus());
        response.setOptions(toOptionResponses(options, includeAnswers));
        return response;
    }

    public ExamRecordResponse toExamRecordResponse(ExamRecord record, String trainingStatus) {
        ExamRecordResponse response = new ExamRecordResponse();
        response.setRecordId(record.getId());
        response.setExamId(record.getExamId());
        response.setAttemptNo(record.getAttemptNo());
        response.setScore(record.getScore());
        response.setPassStatus(record.getPassStatus());
        response.setTrainingStatus(trainingStatus);
        return response;
    }

    private Map<Long, ExamQuestion> loadQuestions(List<TrainingExamQuestion> relations) {
        if (relations.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> questionIds = relations.stream()
                .map(TrainingExamQuestion::getQuestionId)
                .collect(Collectors.toList());
        Map<Long, ExamQuestion> result = new LinkedHashMap<>();
        for (ExamQuestion question : support.selectQuestions(questionIds)) {
            result.put(question.getId(), question);
        }
        return result;
    }

    private Map<Long, List<ExamQuestionOption>> loadOptions(List<Long> questionIds) {
        if (questionIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<ExamQuestionOption> options = optionMapper.selectList(new QueryWrapper<ExamQuestionOption>()
                .in("question_id", questionIds)
                .orderByAsc("question_id").orderByAsc("sort_no").orderByAsc("id"));
        Map<Long, List<ExamQuestionOption>> result = new LinkedHashMap<>();
        for (ExamQuestionOption option : options) {
            result.computeIfAbsent(option.getQuestionId(), key -> new ArrayList<>()).add(option);
        }
        return result;
    }

    private List<QuestionOptionResponse> toOptionResponses(Long questionId, boolean includeAnswers) {
        List<ExamQuestionOption> options = optionMapper.selectList(new QueryWrapper<ExamQuestionOption>()
                .eq("question_id", questionId)
                .orderByAsc("sort_no").orderByAsc("id"));
        return toOptionResponses(options, includeAnswers);
    }

    private List<QuestionOptionResponse> toOptionResponses(List<ExamQuestionOption> options, boolean includeAnswers) {
        List<QuestionOptionResponse> responses = new ArrayList<>();
        for (ExamQuestionOption option : options) {
            QuestionOptionResponse response = new QuestionOptionResponse();
            response.setId(option.getId());
            response.setOptionLabel(option.getOptionLabel());
            response.setOptionContent(option.getOptionContent());
            response.setCorrect(includeAnswers ? Integer.valueOf(1).equals(option.getIsCorrect()) : null);
            response.setSortNo(option.getSortNo());
            responses.add(response);
        }
        return responses;
    }
}
