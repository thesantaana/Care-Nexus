package com.carenexus;

import com.carenexus.ai.mapper.AiDraftMapper;
import com.carenexus.ai.mapper.AiDraftSourceResourceMapper;
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
}
