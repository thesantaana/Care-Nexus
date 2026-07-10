package com.carenexus.training;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.carenexus.training.entity.ExamQuestion;
import com.carenexus.training.entity.TrainingExam;
import com.carenexus.training.entity.TrainingExamQuestion;
import com.carenexus.training.exam.service.TrainingExamAssembler;
import com.carenexus.training.exam.service.TrainingExamSupport;
import com.carenexus.training.mapper.ExamQuestionOptionMapper;
import com.carenexus.training.mapper.TrainingExamQuestionMapper;
import com.carenexus.training.vo.ExamResponse;
import java.math.BigDecimal;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class TrainingExamAssemblerTest {

    @Test
    void shouldAssembleTrueFalseQuestionWithoutOptions() {
        TrainingExamSupport support = mock(TrainingExamSupport.class);
        TrainingExamQuestionMapper relationMapper = mock(TrainingExamQuestionMapper.class);
        ExamQuestionOptionMapper optionMapper = mock(ExamQuestionOptionMapper.class);
        TrainingExamAssembler assembler = new TrainingExamAssembler(support, relationMapper, optionMapper);

        TrainingExamQuestion relation = new TrainingExamQuestion();
        relation.setExamId(1L);
        relation.setQuestionId(11L);
        relation.setScore(new BigDecimal("5.00"));
        relation.setSortNo(1);
        ExamQuestion question = new ExamQuestion();
        question.setId(11L);
        question.setQuestionType("TRUE_FALSE");
        question.setQuestionContent("判断题");
        question.setQuestionStatus("PUBLISHED");
        TrainingExam exam = new TrainingExam();
        exam.setId(1L);
        exam.setExamName("测试考核");

        when(relationMapper.selectByExamId(1L)).thenReturn(Collections.singletonList(relation));
        when(support.selectQuestions(Collections.singletonList(11L))).thenReturn(Collections.singletonList(question));
        when(optionMapper.selectList(any())).thenReturn(Collections.emptyList());

        ExamResponse response = assembler.toExamResponse(exam, false);

        assertEquals(1, response.getQuestions().size());
        assertTrue(response.getQuestions().get(0).getQuestion().getOptions().isEmpty());
    }
}
