package com.carenexus;

import com.carenexus.ai.mapper.AiDraftMapper;
import com.carenexus.ai.mapper.AiDraftSourceResourceMapper;
import com.carenexus.training.mapper.TrainingNoteMapper;
import com.carenexus.training.mapper.LearningResourceFavoriteMapper;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CareMapperTestConfiguration {

    @Bean
    public AiDraftMapper aiDraftMapper() {
        return Mockito.mock(AiDraftMapper.class);
    }

    @Bean
    public AiDraftSourceResourceMapper aiDraftSourceResourceMapper() {
        return Mockito.mock(AiDraftSourceResourceMapper.class);
    }

    @Bean
    public TrainingNoteMapper trainingNoteMapper() {
        return Mockito.mock(TrainingNoteMapper.class);
    }

    @Bean
    public LearningResourceFavoriteMapper learningResourceFavoriteMapper() {
        return Mockito.mock(LearningResourceFavoriteMapper.class);
    }
}
