package com.carenexus.ai.service;

import com.carenexus.ai.TrainingAiSource;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.training.resource.service.TrainingResourceQueryService;
import com.carenexus.training.vo.ResourceResponse;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TrainingAiSourceService {
    private final TrainingResourceQueryService resourceQueryService;

    public TrainingAiSourceService(TrainingResourceQueryService resourceQueryService) {
        this.resourceQueryService = resourceQueryService;
    }

    public List<TrainingAiSource> load(List<Long> sourceResourceIds) {
        if (sourceResourceIds == null || sourceResourceIds.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "sourceResourceIds cannot be empty");
        }
        Set<Long> uniqueIds = new LinkedHashSet<>(sourceResourceIds);
        if (uniqueIds.contains(null)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Resource id cannot be null");
        }
        List<TrainingAiSource> sources = new ArrayList<>();
        for (Long resourceId : uniqueIds) {
            ResourceResponse resource = resourceQueryService.getResource(resourceId);
            sources.add(new TrainingAiSource(resource.getId(), resource.getTitle(), readableContent(resource)));
        }
        return sources;
    }

    private String readableContent(ResourceResponse resource) {
        if (StringUtils.hasText(resource.getContent())) {
            return resource.getContent().trim();
        }
        if (StringUtils.hasText(resource.getSummary())) {
            return "课程名称：" + resource.getTitle() + "。课程摘要：" + resource.getSummary().trim();
        }
        throw new BusinessException(ErrorCode.BAD_REQUEST,
                "培训资料“" + resource.getTitle() + "”暂无可供 AI 使用的正文或摘要");
    }
}
