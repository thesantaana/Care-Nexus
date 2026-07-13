package com.carenexus.training.resource.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carenexus.auth.CurrentUser;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.common.response.PageResponse;
import com.carenexus.training.constant.TrainingResourceStatus;
import com.carenexus.training.constant.TrainingResourceType;
import com.carenexus.training.entity.TrainingCategory;
import com.carenexus.training.entity.TrainingResource;
import com.carenexus.training.mapper.TrainingCategoryMapper;
import com.carenexus.training.mapper.TrainingResourceMapper;
import com.carenexus.training.resource.support.TrainingResourceAccessPolicy;
import com.carenexus.training.support.TrainingText;
import com.carenexus.training.vo.CategoryResponse;
import com.carenexus.training.vo.ResourceResponse;
import com.carenexus.training.vo.ResourceSummaryResponse;
import com.carenexus.training.vo.TagResponse;
import com.carenexus.training.vo.UserBriefResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TrainingResourceQueryService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final int ACTIVE_FLAG = 0;

    private final TrainingResourceAccessPolicy accessPolicy;
    private final TrainingResourceMapper resourceMapper;
    private final TrainingCategoryMapper categoryMapper;
    private final TrainingResourceTagService tagService;

    public TrainingResourceQueryService(TrainingResourceAccessPolicy accessPolicy,
            TrainingResourceMapper resourceMapper, TrainingCategoryMapper categoryMapper,
            TrainingResourceTagService tagService) {
        this.accessPolicy = accessPolicy;
        this.resourceMapper = resourceMapper;
        this.categoryMapper = categoryMapper;
        this.tagService = tagService;
    }

    public PageResponse<ResourceSummaryResponse> pageResources(String keyword, String resourceType, Long categoryId,
            Long tagId, String status, int pageNo, int pageSize) {
        CurrentUser currentUser = accessPolicy.requireViewOrManage();
        int safePageNo = validatePageNo(pageNo);
        int safePageSize = validatePageSize(pageSize);
        List<Long> taggedResourceIds = tagService.selectResourceIdsByTag(tagId);
        if (tagId != null && taggedResourceIds.isEmpty()) {
            return PageResponse.empty(safePageNo, safePageSize);
        }

        QueryWrapper<TrainingResource> wrapper = resourceQuery(keyword, resourceType, categoryId, taggedResourceIds);
        applyVisibility(wrapper, currentUser, TrainingText.optional(status));
        wrapper.orderByDesc("published_at").orderByDesc("updated_at").orderByDesc("id");
        Page<TrainingResource> page = resourceMapper.selectPage(new Page<TrainingResource>(safePageNo, safePageSize),
                wrapper);
        return PageResponse.from(page, toSummaryResponses(page.getRecords()));
    }

    public ResourceResponse getResource(Long id) {
        CurrentUser currentUser = accessPolicy.requireViewOrManage();
        TrainingResource resource = requireResource(id);
        if (!accessPolicy.hasManage(currentUser)
                && !TrainingResourceStatus.PUBLISHED.equals(resource.getResourceStatus())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Training resource not found");
        }
        return toResourceResponse(resource);
    }

    public ResourceResponse toResourceResponse(TrainingResource resource) {
        Map<Long, TrainingCategory> categories = loadCategories(Collections.singletonList(resource));
        Map<Long, List<TagResponse>> tags = tagService.loadTagsByResourceIds(Collections.singletonList(resource.getId()));
        return toResourceResponse(resource, categories, tags);
    }

    private List<ResourceSummaryResponse> toSummaryResponses(List<TrainingResource> resources) {
        if (resources == null || resources.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, TrainingCategory> categories = loadCategories(resources);
        List<Long> resourceIds = resources.stream().map(TrainingResource::getId).collect(Collectors.toList());
        Map<Long, List<TagResponse>> tags = tagService.loadTagsByResourceIds(resourceIds);
        return resources.stream().map(resource -> toSummaryResponse(resource, categories, tags))
                .collect(Collectors.toList());
    }

    private QueryWrapper<TrainingResource> resourceQuery(String keyword, String resourceType, Long categoryId,
            List<Long> taggedResourceIds) {
        QueryWrapper<TrainingResource> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", ACTIVE_FLAG);
        String type = TrainingText.optional(resourceType);
        if (StringUtils.hasText(type)) {
            validateResourceType(type);
            wrapper.eq("resource_type", type);
        }
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        String safeKeyword = TrainingText.optional(keyword);
        if (StringUtils.hasText(safeKeyword)) {
            wrapper.and(query -> query.like("title", safeKeyword).or().like("summary", safeKeyword));
        }
        if (taggedResourceIds != null && !taggedResourceIds.isEmpty()) {
            wrapper.in("id", taggedResourceIds);
        }
        return wrapper;
    }

    private void applyVisibility(QueryWrapper<TrainingResource> wrapper, CurrentUser currentUser, String status) {
        if (accessPolicy.hasManage(currentUser)) {
            if (StringUtils.hasText(status)) {
                validateResourceStatus(status);
                wrapper.eq("resource_status", status);
            }
            return;
        }
        if (StringUtils.hasText(status)) {
            validateResourceStatus(status);
            if (!TrainingResourceStatus.PUBLISHED.equals(status)) {
                wrapper.eq("resource_status", "__NO_VISIBLE_STATUS__");
                return;
            }
        }
        wrapper.eq("resource_status", TrainingResourceStatus.PUBLISHED);
    }

    private TrainingResource requireResource(Long id) {
        TrainingResource resource = resourceMapper.selectById(id);
        if (resource == null || Integer.valueOf(1).equals(resource.getIsDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Training resource not found");
        }
        return resource;
    }

    private Map<Long, TrainingCategory> loadCategories(List<TrainingResource> resources) {
        Set<Long> categoryIds = resources.stream().map(TrainingResource::getCategoryId)
                .filter(id -> id != null).collect(Collectors.toSet());
        if (categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Collection<TrainingCategory> categories = categoryMapper.selectBatchIds(categoryIds);
        return categories.stream().collect(Collectors.toMap(TrainingCategory::getId, category -> category));
    }

    private ResourceResponse toResourceResponse(TrainingResource resource, Map<Long, TrainingCategory> categories,
            Map<Long, List<TagResponse>> tags) {
        ResourceResponse response = new ResourceResponse();
        response.setId(resource.getId());
        response.setResourceType(resource.getResourceType());
        response.setStorageMode(resource.getStorageMode());
        response.setCategoryId(resource.getCategoryId());
        response.setCategory(toCategoryResponse(categories.get(resource.getCategoryId())));
        response.setTitle(resource.getTitle());
        response.setSummary(resource.getSummary());
        response.setCoverUrl(resource.getCoverUrl());
        response.setContent(resource.getContent());
        response.setFileResourceId(resource.getFileResourceId());
        response.setExternalUrl(resource.getExternalUrl());
        response.setDurationSeconds(resource.getDurationSeconds());
        response.setStatus(resource.getResourceStatus());
        response.setPublishedAt(resource.getPublishedAt());
        response.setCreatedBy(userBrief(resource.getCreatedBy()));
        response.setUpdatedBy(userBrief(resource.getUpdatedBy()));
        response.setTags(tags.getOrDefault(resource.getId(), Collections.emptyList()));
        return response;
    }

    private ResourceSummaryResponse toSummaryResponse(TrainingResource resource,
            Map<Long, TrainingCategory> categories, Map<Long, List<TagResponse>> tags) {
        ResourceSummaryResponse response = new ResourceSummaryResponse();
        TrainingCategory category = categories.get(resource.getCategoryId());
        response.setId(resource.getId());
        response.setResourceType(resource.getResourceType());
        response.setStorageMode(resource.getStorageMode());
        response.setCategoryId(resource.getCategoryId());
        response.setCategoryName(category == null ? null : category.getCategoryName());
        response.setTitle(resource.getTitle());
        response.setSummary(resource.getSummary());
        response.setCoverUrl(resource.getCoverUrl());
        response.setDurationSeconds(resource.getDurationSeconds());
        response.setStatus(resource.getResourceStatus());
        response.setPublishedAt(resource.getPublishedAt());
        response.setTags(tags.getOrDefault(resource.getId(), Collections.emptyList()));
        return response;
    }

    private CategoryResponse toCategoryResponse(TrainingCategory category) {
        if (category == null || Integer.valueOf(1).equals(category.getIsDeleted())) {
            return null;
        }
        return new CategoryResponse(category.getId(), category.getCategoryName(), category.getSortNo(),
                category.getCategoryStatus());
    }

    private UserBriefResponse userBrief(Long userId) {
        return userId == null ? null : new UserBriefResponse(userId, null, null);
    }

    private int validatePageNo(int pageNo) {
        if (pageNo < 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "pageNo must be greater than or equal to 1");
        }
        return pageNo;
    }

    private int validatePageSize(int pageSize) {
        if (pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "pageSize must be between 1 and 100");
        }
        return pageSize;
    }

    private void validateResourceType(String resourceType) {
        if (!Arrays.asList(TrainingResourceType.ARTICLE, TrainingResourceType.VIDEO, TrainingResourceType.PPT)
                .contains(resourceType)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid resource type");
        }
    }

    private void validateResourceStatus(String status) {
        if (!Arrays.asList(TrainingResourceStatus.DRAFT, TrainingResourceStatus.PUBLISHED,
                TrainingResourceStatus.OFFLINE).contains(status)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid resource status");
        }
    }
}
