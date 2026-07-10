package com.carenexus.training.resource.service;

import com.carenexus.training.catalog.service.TrainingCatalogService;
import com.carenexus.training.entity.TrainingResourceTag;
import com.carenexus.training.entity.TrainingTag;
import com.carenexus.training.mapper.TrainingResourceTagMapper;
import com.carenexus.training.mapper.TrainingTagMapper;
import com.carenexus.training.vo.TagResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class TrainingResourceTagService {

    private final TrainingCatalogService catalogService;
    private final TrainingTagMapper tagMapper;
    private final TrainingResourceTagMapper resourceTagMapper;

    public TrainingResourceTagService(TrainingCatalogService catalogService, TrainingTagMapper tagMapper,
            TrainingResourceTagMapper resourceTagMapper) {
        this.catalogService = catalogService;
        this.tagMapper = tagMapper;
        this.resourceTagMapper = resourceTagMapper;
    }

    public void validateEnabledTagIds(List<Long> tagIds) {
        for (Long tagId : uniqueTagIds(tagIds)) {
            catalogService.requireEnabledTag(tagId);
        }
    }

    public void replaceResourceTags(Long resourceId, List<Long> tagIds) {
        resourceTagMapper.deleteByResourceId(resourceId);
        for (Long tagId : uniqueTagIds(tagIds)) {
            TrainingResourceTag relation = new TrainingResourceTag();
            relation.setResourceId(resourceId);
            relation.setTagId(tagId);
            resourceTagMapper.insert(relation);
        }
    }

    public List<Long> selectResourceIdsByTag(Long tagId) {
        return tagId == null ? null : resourceTagMapper.selectResourceIdsByTagId(tagId);
    }

    public Map<Long, List<TagResponse>> loadTagsByResourceIds(List<Long> resourceIds) {
        if (resourceIds == null || resourceIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<TrainingResourceTag> relations = resourceTagMapper.selectByResourceIds(resourceIds);
        if (relations == null || relations.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, TrainingTag> tagsById = loadTagsById(relations);
        Map<Long, List<TagResponse>> result = new LinkedHashMap<>();
        for (TrainingResourceTag relation : relations) {
            TrainingTag tag = tagsById.get(relation.getTagId());
            if (tag != null && !Integer.valueOf(1).equals(tag.getIsDeleted())) {
                result.computeIfAbsent(relation.getResourceId(), key -> new ArrayList<>()).add(toTagResponse(tag));
            }
        }
        return result;
    }

    private Map<Long, TrainingTag> loadTagsById(List<TrainingResourceTag> relations) {
        Set<Long> tagIds = relations.stream().map(TrainingResourceTag::getTagId).collect(Collectors.toSet());
        if (tagIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Collection<TrainingTag> tags = tagMapper.selectBatchIds(tagIds);
        return tags.stream().collect(Collectors.toMap(TrainingTag::getId, tag -> tag));
    }

    private Set<Long> uniqueTagIds(List<Long> tagIds) {
        if (tagIds == null) {
            return Collections.emptySet();
        }
        Set<Long> unique = new LinkedHashSet<>();
        for (Long tagId : tagIds) {
            if (tagId != null) {
                unique.add(tagId);
            }
        }
        return unique;
    }

    private TagResponse toTagResponse(TrainingTag tag) {
        return new TagResponse(tag.getId(), tag.getTagName(), tag.getTagStatus());
    }
}
