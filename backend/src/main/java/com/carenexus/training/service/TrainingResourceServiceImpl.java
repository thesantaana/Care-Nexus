package com.carenexus.training.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carenexus.audit.OperationLogService;
import com.carenexus.auth.CurrentUser;
import com.carenexus.auth.CurrentUserService;
import com.carenexus.auth.PermissionService;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.common.response.PageResponse;
import com.carenexus.file.entity.FileResource;
import com.carenexus.file.mapper.FileResourceMapper;
import com.carenexus.training.constant.TrainingPermissions;
import com.carenexus.training.constant.TrainingStatuses;
import com.carenexus.training.dto.CategoryRequest;
import com.carenexus.training.dto.OfflineRequest;
import com.carenexus.training.dto.ResourceRequest;
import com.carenexus.training.dto.StatusRequest;
import com.carenexus.training.dto.TagRequest;
import com.carenexus.training.entity.TrainingCategory;
import com.carenexus.training.entity.TrainingResource;
import com.carenexus.training.entity.TrainingResourceTag;
import com.carenexus.training.entity.TrainingTag;
import com.carenexus.training.mapper.TrainingCategoryMapper;
import com.carenexus.training.mapper.TrainingResourceMapper;
import com.carenexus.training.mapper.TrainingResourceTagMapper;
import com.carenexus.training.mapper.TrainingTagMapper;
import com.carenexus.training.vo.CategoryResponse;
import com.carenexus.training.vo.ResourceResponse;
import com.carenexus.training.vo.ResourceSummaryResponse;
import com.carenexus.training.vo.TagResponse;
import com.carenexus.training.vo.UserBriefResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class TrainingResourceServiceImpl implements TrainingResourceService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final int ACTIVE_FLAG = 0;
    private static final String FILE_STATUS_ACTIVE = "ACTIVE";
    private static final String LOG_ACTION_PUBLISH = "TRAINING_RESOURCE_PUBLISH";
    private static final String LOG_ACTION_OFFLINE = "TRAINING_RESOURCE_OFFLINE";
    private static final String LOG_TARGET_RESOURCE = "TRAINING_RESOURCE";
    private static final String LOG_RESULT_SUCCESS = "SUCCESS";

    private final CurrentUserService currentUserService;
    private final PermissionService permissionService;
    private final OperationLogService operationLogService;
    private final TrainingCategoryMapper categoryMapper;
    private final TrainingTagMapper tagMapper;
    private final TrainingResourceMapper resourceMapper;
    private final TrainingResourceTagMapper resourceTagMapper;
    private final FileResourceMapper fileResourceMapper;

    public TrainingResourceServiceImpl(CurrentUserService currentUserService, PermissionService permissionService,
            OperationLogService operationLogService, TrainingCategoryMapper categoryMapper,
            TrainingTagMapper tagMapper, TrainingResourceMapper resourceMapper,
            TrainingResourceTagMapper resourceTagMapper, FileResourceMapper fileResourceMapper) {
        this.currentUserService = currentUserService;
        this.permissionService = permissionService;
        this.operationLogService = operationLogService;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.resourceMapper = resourceMapper;
        this.resourceTagMapper = resourceTagMapper;
        this.fileResourceMapper = fileResourceMapper;
    }

    @Override
    public List<CategoryResponse> listCategories(String status) {
        CurrentUser currentUser = requireViewOrManage();
        boolean manage = hasManage(currentUser);
        QueryWrapper<TrainingCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", ACTIVE_FLAG);
        String queryStatus = manage ? normalizeOptional(status) : TrainingStatuses.ENABLED;
        if (StringUtils.hasText(queryStatus)) {
            validateEnabledDisabled(queryStatus);
            wrapper.eq("category_status", queryStatus);
        }
        wrapper.orderByAsc("sort_no").orderByAsc("id");
        return categoryMapper.selectList(wrapper).stream().map(this::toCategoryResponse).collect(Collectors.toList());
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        requireManage();
        String name = normalizeRequired(request.getCategoryName(), "Category name is required");
        ensureUniqueCategoryName(name, null);
        TrainingCategory category = new TrainingCategory();
        category.setCategoryName(name);
        category.setSortNo(request.getSortNo());
        category.setCategoryStatus(TrainingStatuses.ENABLED);
        category.setIsDeleted(ACTIVE_FLAG);
        categoryMapper.insert(category);
        return toCategoryResponse(category);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        requireManage();
        TrainingCategory category = requireCategory(id);
        String name = normalizeRequired(request.getCategoryName(), "Category name is required");
        ensureUniqueCategoryName(name, id);
        category.setCategoryName(name);
        category.setSortNo(request.getSortNo());
        categoryMapper.updateById(category);
        return toCategoryResponse(category);
    }

    @Override
    public CategoryResponse updateCategoryStatus(Long id, StatusRequest request) {
        requireManage();
        TrainingCategory category = requireCategory(id);
        String status = normalizeRequired(request.getStatus(), "Status is required");
        validateEnabledDisabled(status);
        category.setCategoryStatus(status);
        categoryMapper.updateById(category);
        return toCategoryResponse(category);
    }

    @Override
    public List<TagResponse> listTags(String status) {
        CurrentUser currentUser = requireViewOrManage();
        boolean manage = hasManage(currentUser);
        QueryWrapper<TrainingTag> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", ACTIVE_FLAG);
        String queryStatus = manage ? normalizeOptional(status) : TrainingStatuses.ENABLED;
        if (StringUtils.hasText(queryStatus)) {
            validateEnabledDisabled(queryStatus);
            wrapper.eq("tag_status", queryStatus);
        }
        wrapper.orderByAsc("id");
        return tagMapper.selectList(wrapper).stream().map(this::toTagResponse).collect(Collectors.toList());
    }

    @Override
    public TagResponse createTag(TagRequest request) {
        requireManage();
        String name = normalizeRequired(request.getTagName(), "Tag name is required");
        ensureUniqueTagName(name, null);
        TrainingTag tag = new TrainingTag();
        tag.setTagName(name);
        tag.setTagStatus(TrainingStatuses.ENABLED);
        tag.setIsDeleted(ACTIVE_FLAG);
        tagMapper.insert(tag);
        return toTagResponse(tag);
    }

    @Override
    public TagResponse updateTag(Long id, TagRequest request) {
        requireManage();
        TrainingTag tag = requireTag(id);
        String name = normalizeRequired(request.getTagName(), "Tag name is required");
        ensureUniqueTagName(name, id);
        tag.setTagName(name);
        tagMapper.updateById(tag);
        return toTagResponse(tag);
    }

    @Override
    public TagResponse updateTagStatus(Long id, StatusRequest request) {
        requireManage();
        TrainingTag tag = requireTag(id);
        String status = normalizeRequired(request.getStatus(), "Status is required");
        validateEnabledDisabled(status);
        tag.setTagStatus(status);
        tagMapper.updateById(tag);
        return toTagResponse(tag);
    }

    @Override
    @Transactional
    public ResourceResponse createResource(ResourceRequest request) {
        CurrentUser currentUser = requireManage();
        TrainingResource resource = new TrainingResource();
        applyResourceRequest(resource, request);
        resource.setResourceStatus(TrainingStatuses.DRAFT);
        resource.setCreatedBy(currentUser.getUserId());
        resource.setUpdatedBy(currentUser.getUserId());
        resource.setIsDeleted(ACTIVE_FLAG);
        resourceMapper.insert(resource);
        replaceResourceTags(resource.getId(), request.getTagIds());
        return toResourceResponse(resource);
    }

    @Override
    @Transactional
    public ResourceResponse updateResource(Long id, ResourceRequest request) {
        CurrentUser currentUser = requireManage();
        TrainingResource resource = requireResource(id);
        if (TrainingStatuses.PUBLISHED.equals(resource.getResourceStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Published resource must be offline before update");
        }
        applyResourceRequest(resource, request);
        resource.setUpdatedBy(currentUser.getUserId());
        resourceMapper.updateById(resource);
        replaceResourceTags(resource.getId(), request.getTagIds());
        return toResourceResponse(resource);
    }

    @Override
    public PageResponse<ResourceSummaryResponse> pageResources(String keyword, String resourceType, Long categoryId,
            Long tagId, String status, int pageNo, int pageSize) {
        CurrentUser currentUser = requireViewOrManage();
        int safePageNo = validatePageNo(pageNo);
        int safePageSize = validatePageSize(pageSize);
        List<Long> taggedResourceIds = resolveResourceIdsByTag(tagId);
        if (tagId != null && taggedResourceIds.isEmpty()) {
            return PageResponse.empty(safePageNo, safePageSize);
        }
        QueryWrapper<TrainingResource> wrapper = resourceQuery(keyword, resourceType, categoryId, taggedResourceIds);
        applyVisibility(wrapper, currentUser, normalizeOptional(status));
        wrapper.orderByDesc("published_at").orderByDesc("updated_at").orderByDesc("id");
        Page<TrainingResource> page = resourceMapper.selectPage(new Page<TrainingResource>(safePageNo, safePageSize),
                wrapper);
        List<ResourceSummaryResponse> records = page.getRecords().stream()
                .map(this::toResourceSummaryResponse)
                .collect(Collectors.toList());
        return PageResponse.from(page, records);
    }

    @Override
    public ResourceResponse getResource(Long id) {
        CurrentUser currentUser = requireViewOrManage();
        TrainingResource resource = requireResource(id);
        if (!hasManage(currentUser) && !TrainingStatuses.PUBLISHED.equals(resource.getResourceStatus())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Training resource not found");
        }
        return toResourceResponse(resource);
    }

    @Override
    @Transactional
    public ResourceResponse publishResource(Long id) {
        CurrentUser currentUser = requireManage();
        TrainingResource resource = requireResource(id);
        if (TrainingStatuses.PUBLISHED.equals(resource.getResourceStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Training resource is already published");
        }
        if (!TrainingStatuses.DRAFT.equals(resource.getResourceStatus())
                && !TrainingStatuses.OFFLINE.equals(resource.getResourceStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Training resource status cannot be published");
        }
        resource.setResourceStatus(TrainingStatuses.PUBLISHED);
        resource.setPublishedAt(LocalDateTime.now());
        resource.setUpdatedBy(currentUser.getUserId());
        resourceMapper.updateById(resource);
        operationLogService.record(currentUser, LOG_ACTION_PUBLISH, LOG_TARGET_RESOURCE, resource.getId(),
                LOG_RESULT_SUCCESS);
        return toResourceResponse(resource);
    }

    @Override
    @Transactional
    public ResourceResponse offlineResource(Long id, OfflineRequest request) {
        CurrentUser currentUser = requireManage();
        normalizeRequired(request.getReason(), "Offline reason is required");
        TrainingResource resource = requireResource(id);
        if (TrainingStatuses.OFFLINE.equals(resource.getResourceStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Training resource is already offline");
        }
        if (!TrainingStatuses.PUBLISHED.equals(resource.getResourceStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Only published resource can be offline");
        }
        resource.setResourceStatus(TrainingStatuses.OFFLINE);
        resource.setUpdatedBy(currentUser.getUserId());
        resourceMapper.updateById(resource);
        operationLogService.record(currentUser, LOG_ACTION_OFFLINE, LOG_TARGET_RESOURCE, resource.getId(),
                LOG_RESULT_SUCCESS);
        return toResourceResponse(resource);
    }

    private QueryWrapper<TrainingResource> resourceQuery(String keyword, String resourceType, Long categoryId,
            List<Long> taggedResourceIds) {
        QueryWrapper<TrainingResource> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", ACTIVE_FLAG);
        String type = normalizeOptional(resourceType);
        if (StringUtils.hasText(type)) {
            validateResourceType(type);
            wrapper.eq("resource_type", type);
        }
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        String safeKeyword = normalizeOptional(keyword);
        if (StringUtils.hasText(safeKeyword)) {
            wrapper.and(query -> query.like("title", safeKeyword).or().like("summary", safeKeyword));
        }
        if (taggedResourceIds != null && !taggedResourceIds.isEmpty()) {
            wrapper.in("id", taggedResourceIds);
        }
        return wrapper;
    }

    private void applyVisibility(QueryWrapper<TrainingResource> wrapper, CurrentUser currentUser, String status) {
        if (hasManage(currentUser)) {
            if (StringUtils.hasText(status)) {
                validateResourceStatus(status);
                wrapper.eq("resource_status", status);
            }
            return;
        }
        if (StringUtils.hasText(status)) {
            validateResourceStatus(status);
            if (!TrainingStatuses.PUBLISHED.equals(status)) {
                wrapper.eq("resource_status", "__NO_VISIBLE_STATUS__");
                return;
            }
        }
        wrapper.eq("resource_status", TrainingStatuses.PUBLISHED);
    }

    private void applyResourceRequest(TrainingResource resource, ResourceRequest request) {
        String type = normalizeRequired(request.getResourceType(), "Resource type is required");
        String storageMode = normalizeRequired(request.getStorageMode(), "Storage mode is required");
        validateResourceType(type);
        validateStorageMode(storageMode);
        TrainingCategory category = requireEnabledCategory(request.getCategoryId());
        validateStoragePayload(storageMode, request);
        validateTagIds(request.getTagIds());
        resource.setResourceType(type);
        resource.setStorageMode(storageMode);
        resource.setCategoryId(category.getId());
        resource.setTitle(normalizeRequired(request.getTitle(), "Resource title is required"));
        resource.setSummary(normalizeOptional(request.getSummary()));
        resource.setDurationSeconds(request.getDurationSeconds());
        applyStoragePayload(resource, storageMode, request);
    }

    private void applyStoragePayload(TrainingResource resource, String storageMode, ResourceRequest request) {
        if (TrainingStatuses.TEXT.equals(storageMode)) {
            resource.setContent(normalizeRequired(request.getContent(), "Content is required for text resource"));
            resource.setFileResourceId(null);
            resource.setExternalUrl(null);
            return;
        }
        if (TrainingStatuses.LOCAL_FILE.equals(storageMode)) {
            resource.setContent(null);
            resource.setFileResourceId(request.getFileResourceId());
            resource.setExternalUrl(null);
            return;
        }
        resource.setContent(null);
        resource.setFileResourceId(null);
        resource.setExternalUrl(normalizeRequired(request.getExternalUrl(),
                "External URL is required for external link resource"));
    }

    private void validateStoragePayload(String storageMode, ResourceRequest request) {
        if (TrainingStatuses.TEXT.equals(storageMode)) {
            normalizeRequired(request.getContent(), "Content is required for text resource");
            return;
        }
        if (TrainingStatuses.LOCAL_FILE.equals(storageMode)) {
            if (request.getFileResourceId() == null) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "File resource is required for local file resource");
            }
            FileResource fileResource = fileResourceMapper.selectById(request.getFileResourceId());
            if (fileResource == null || !FILE_STATUS_ACTIVE.equals(fileResource.getFileStatus())
                    || Integer.valueOf(1).equals(fileResource.getIsDeleted())) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "File resource not found");
            }
            return;
        }
        String url = normalizeRequired(request.getExternalUrl(), "External URL is required for external link resource");
        validateHttpUrl(url);
    }

    private TrainingCategory requireCategory(Long id) {
        TrainingCategory category = categoryMapper.selectById(id);
        if (category == null || Integer.valueOf(1).equals(category.getIsDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Training category not found");
        }
        return category;
    }

    private TrainingCategory requireEnabledCategory(Long id) {
        TrainingCategory category = requireCategory(id);
        if (!TrainingStatuses.ENABLED.equals(category.getCategoryStatus())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Training category not found");
        }
        return category;
    }

    private TrainingTag requireTag(Long id) {
        TrainingTag tag = tagMapper.selectById(id);
        if (tag == null || Integer.valueOf(1).equals(tag.getIsDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Training tag not found");
        }
        return tag;
    }

    private TrainingResource requireResource(Long id) {
        TrainingResource resource = resourceMapper.selectById(id);
        if (resource == null || Integer.valueOf(1).equals(resource.getIsDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Training resource not found");
        }
        return resource;
    }

    private void ensureUniqueCategoryName(String name, Long excludeId) {
        QueryWrapper<TrainingCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("category_name", name).eq("is_deleted", ACTIVE_FLAG);
        if (excludeId != null) {
            wrapper.ne("id", excludeId);
        }
        if (categoryMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "Training category name already exists");
        }
    }

    private void ensureUniqueTagName(String name, Long excludeId) {
        QueryWrapper<TrainingTag> wrapper = new QueryWrapper<>();
        wrapper.eq("tag_name", name).eq("is_deleted", ACTIVE_FLAG);
        if (excludeId != null) {
            wrapper.ne("id", excludeId);
        }
        if (tagMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "Training tag name already exists");
        }
    }

    private void validateTagIds(List<Long> tagIds) {
        Set<Long> uniqueTagIds = uniqueTagIds(tagIds);
        for (Long tagId : uniqueTagIds) {
            TrainingTag tag = requireTag(tagId);
            if (!TrainingStatuses.ENABLED.equals(tag.getTagStatus())) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "Training tag not found");
            }
        }
    }

    private void replaceResourceTags(Long resourceId, List<Long> tagIds) {
        resourceTagMapper.deleteByResourceId(resourceId);
        for (Long tagId : uniqueTagIds(tagIds)) {
            TrainingResourceTag relation = new TrainingResourceTag();
            relation.setResourceId(resourceId);
            relation.setTagId(tagId);
            resourceTagMapper.insert(relation);
        }
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

    private List<Long> resolveResourceIdsByTag(Long tagId) {
        if (tagId == null) {
            return null;
        }
        return resourceTagMapper.selectResourceIdsByTagId(tagId);
    }

    private List<TagResponse> tagsOf(Long resourceId) {
        List<Long> tagIds = resourceTagMapper.selectTagIdsByResourceId(resourceId);
        if (tagIds == null || tagIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<TagResponse> responses = new ArrayList<>();
        for (Long tagId : tagIds) {
            TrainingTag tag = tagMapper.selectById(tagId);
            if (tag != null && !Integer.valueOf(1).equals(tag.getIsDeleted())) {
                responses.add(toTagResponse(tag));
            }
        }
        return responses;
    }

    private CategoryResponse categoryOf(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        TrainingCategory category = categoryMapper.selectById(categoryId);
        if (category == null || Integer.valueOf(1).equals(category.getIsDeleted())) {
            return null;
        }
        return toCategoryResponse(category);
    }

    private CurrentUser requireViewOrManage() {
        CurrentUser currentUser = requireCurrentUser();
        if (!hasView(currentUser) && !hasManage(currentUser)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Permission denied");
        }
        return currentUser;
    }

    private CurrentUser requireManage() {
        CurrentUser currentUser = requireCurrentUser();
        if (!hasManage(currentUser)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Permission denied");
        }
        return currentUser;
    }

    private CurrentUser requireCurrentUser() {
        return currentUserService.getCurrentUser()
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "Authentication required"));
    }

    private boolean hasView(CurrentUser currentUser) {
        return permissionService.hasPermission(currentUser, TrainingPermissions.RESOURCE_VIEW);
    }

    private boolean hasManage(CurrentUser currentUser) {
        return permissionService.hasPermission(currentUser, TrainingPermissions.RESOURCE_MANAGE);
    }

    private CategoryResponse toCategoryResponse(TrainingCategory category) {
        return new CategoryResponse(category.getId(), category.getCategoryName(), category.getSortNo(),
                category.getCategoryStatus());
    }

    private TagResponse toTagResponse(TrainingTag tag) {
        return new TagResponse(tag.getId(), tag.getTagName(), tag.getTagStatus());
    }

    private ResourceResponse toResourceResponse(TrainingResource resource) {
        ResourceResponse response = new ResourceResponse();
        response.setId(resource.getId());
        response.setResourceType(resource.getResourceType());
        response.setStorageMode(resource.getStorageMode());
        response.setCategoryId(resource.getCategoryId());
        response.setCategory(categoryOf(resource.getCategoryId()));
        response.setTitle(resource.getTitle());
        response.setSummary(resource.getSummary());
        response.setContent(resource.getContent());
        response.setFileResourceId(resource.getFileResourceId());
        response.setExternalUrl(resource.getExternalUrl());
        response.setDurationSeconds(resource.getDurationSeconds());
        response.setStatus(resource.getResourceStatus());
        response.setPublishedAt(resource.getPublishedAt());
        response.setCreatedBy(userBrief(resource.getCreatedBy()));
        response.setUpdatedBy(userBrief(resource.getUpdatedBy()));
        response.setTags(tagsOf(resource.getId()));
        return response;
    }

    private ResourceSummaryResponse toResourceSummaryResponse(TrainingResource resource) {
        ResourceSummaryResponse response = new ResourceSummaryResponse();
        CategoryResponse category = categoryOf(resource.getCategoryId());
        response.setId(resource.getId());
        response.setResourceType(resource.getResourceType());
        response.setStorageMode(resource.getStorageMode());
        response.setCategoryId(resource.getCategoryId());
        response.setCategoryName(category == null ? null : category.getCategoryName());
        response.setTitle(resource.getTitle());
        response.setSummary(resource.getSummary());
        response.setDurationSeconds(resource.getDurationSeconds());
        response.setStatus(resource.getResourceStatus());
        response.setPublishedAt(resource.getPublishedAt());
        response.setTags(tagsOf(resource.getId()));
        return response;
    }

    private UserBriefResponse userBrief(Long userId) {
        if (userId == null) {
            return null;
        }
        return new UserBriefResponse(userId, null, null);
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

    private void validateEnabledDisabled(String status) {
        validateOneOf(status, Arrays.asList(TrainingStatuses.ENABLED, TrainingStatuses.DISABLED), "Invalid status");
    }

    private void validateResourceType(String resourceType) {
        validateOneOf(resourceType, Arrays.asList(TrainingStatuses.ARTICLE, TrainingStatuses.VIDEO,
                TrainingStatuses.PPT), "Invalid resource type");
    }

    private void validateStorageMode(String storageMode) {
        validateOneOf(storageMode, Arrays.asList(TrainingStatuses.TEXT, TrainingStatuses.LOCAL_FILE,
                TrainingStatuses.EXTERNAL_LINK), "Invalid storage mode");
    }

    private void validateResourceStatus(String status) {
        validateOneOf(status, Arrays.asList(TrainingStatuses.DRAFT, TrainingStatuses.PUBLISHED,
                TrainingStatuses.OFFLINE), "Invalid resource status");
    }

    private void validateOneOf(String value, List<String> allowed, String message) {
        if (!allowed.contains(value)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, message);
        }
    }

    private void validateHttpUrl(String url) {
        try {
            URI uri = new URI(url);
            if (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "External URL must be http or https");
            }
            if (!StringUtils.hasText(uri.getHost())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "External URL host is required");
            }
        } catch (URISyntaxException ex) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "External URL format is invalid");
        }
    }

    private String normalizeRequired(String value, String message) {
        String normalized = normalizeOptional(value);
        if (!StringUtils.hasText(normalized)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, message);
        }
        return normalized;
    }

    private String normalizeOptional(String value) {
        return value == null ? null : value.trim();
    }
}
