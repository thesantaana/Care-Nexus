package com.carenexus.training.resource.service;

import com.carenexus.audit.OperationLogService;
import com.carenexus.auth.CurrentUser;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.file.FileResourceService;
import com.carenexus.training.catalog.service.TrainingCatalogService;
import com.carenexus.training.constant.TrainingResourceStatus;
import com.carenexus.training.constant.TrainingResourceType;
import com.carenexus.training.constant.TrainingStorageMode;
import com.carenexus.training.dto.OfflineRequest;
import com.carenexus.training.dto.ResourceRequest;
import com.carenexus.training.entity.TrainingCategory;
import com.carenexus.training.entity.TrainingResource;
import com.carenexus.training.mapper.TrainingResourceMapper;
import com.carenexus.training.resource.support.TrainingResourceAccessPolicy;
import com.carenexus.training.support.TrainingText;
import com.carenexus.training.vo.ResourceResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainingResourceService {

    private static final int ACTIVE_FLAG = 0;
    private static final String LOG_ACTION_PUBLISH = "TRAINING_RESOURCE_PUBLISH";
    private static final String LOG_ACTION_OFFLINE = "TRAINING_RESOURCE_OFFLINE";
    private static final String LOG_TARGET_RESOURCE = "TRAINING_RESOURCE";
    private static final String LOG_RESULT_SUCCESS = "SUCCESS";

    private final TrainingResourceAccessPolicy accessPolicy;
    private final OperationLogService operationLogService;
    private final FileResourceService fileResourceService;
    private final TrainingCatalogService catalogService;
    private final TrainingResourceTagService tagService;
    private final TrainingResourceQueryService queryService;
    private final TrainingResourceMapper resourceMapper;

    public TrainingResourceService(TrainingResourceAccessPolicy accessPolicy, OperationLogService operationLogService,
            FileResourceService fileResourceService, TrainingCatalogService catalogService,
            TrainingResourceTagService tagService, TrainingResourceQueryService queryService,
            TrainingResourceMapper resourceMapper) {
        this.accessPolicy = accessPolicy;
        this.operationLogService = operationLogService;
        this.fileResourceService = fileResourceService;
        this.catalogService = catalogService;
        this.tagService = tagService;
        this.queryService = queryService;
        this.resourceMapper = resourceMapper;
    }

    @Transactional
    public ResourceResponse createResource(ResourceRequest request) {
        CurrentUser currentUser = accessPolicy.requireManage();
        TrainingResource resource = new TrainingResource();
        applyResourceRequest(resource, request);
        resource.setResourceStatus(TrainingResourceStatus.DRAFT);
        resource.setCreatedBy(currentUser.getUserId());
        resource.setUpdatedBy(currentUser.getUserId());
        resource.setIsDeleted(ACTIVE_FLAG);
        resourceMapper.insert(resource);
        tagService.replaceResourceTags(resource.getId(), request.getTagIds());
        return queryService.toResourceResponse(resource);
    }

    @Transactional
    public ResourceResponse updateResource(Long id, ResourceRequest request) {
        CurrentUser currentUser = accessPolicy.requireManage();
        TrainingResource resource = requireResource(id);
        if (TrainingResourceStatus.PUBLISHED.equals(resource.getResourceStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Published resource must be offline before update");
        }
        applyResourceRequest(resource, request);
        resource.setUpdatedBy(currentUser.getUserId());
        resourceMapper.updateById(resource);
        tagService.replaceResourceTags(resource.getId(), request.getTagIds());
        return queryService.toResourceResponse(resource);
    }

    @Transactional
    public ResourceResponse publishResource(Long id) {
        CurrentUser currentUser = accessPolicy.requireManage();
        TrainingResource resource = requireResource(id);
        if (TrainingResourceStatus.PUBLISHED.equals(resource.getResourceStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Training resource is already published");
        }
        if (!TrainingResourceStatus.DRAFT.equals(resource.getResourceStatus())
                && !TrainingResourceStatus.OFFLINE.equals(resource.getResourceStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Training resource status cannot be published");
        }
        resource.setResourceStatus(TrainingResourceStatus.PUBLISHED);
        resource.setPublishedAt(LocalDateTime.now());
        resource.setUpdatedBy(currentUser.getUserId());
        resourceMapper.updateById(resource);
        operationLogService.record(currentUser, LOG_ACTION_PUBLISH, LOG_TARGET_RESOURCE, resource.getId(),
                LOG_RESULT_SUCCESS);
        return queryService.toResourceResponse(resource);
    }

    @Transactional
    public ResourceResponse offlineResource(Long id, OfflineRequest request) {
        CurrentUser currentUser = accessPolicy.requireManage();
        String reason = TrainingText.required(request.getReason(), "Offline reason is required");
        TrainingResource resource = requireResource(id);
        if (TrainingResourceStatus.OFFLINE.equals(resource.getResourceStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Training resource is already offline");
        }
        if (!TrainingResourceStatus.PUBLISHED.equals(resource.getResourceStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Only published resource can be offline");
        }
        resource.setResourceStatus(TrainingResourceStatus.OFFLINE);
        resource.setUpdatedBy(currentUser.getUserId());
        resourceMapper.updateById(resource);
        operationLogService.record(currentUser, LOG_ACTION_OFFLINE, LOG_TARGET_RESOURCE, resource.getId(),
                LOG_RESULT_SUCCESS, "offline reason: " + reason);
        return queryService.toResourceResponse(resource);
    }

    private void applyResourceRequest(TrainingResource resource, ResourceRequest request) {
        String type = TrainingText.required(request.getResourceType(), "Resource type is required");
        String storageMode = TrainingText.required(request.getStorageMode(), "Storage mode is required");
        validateResourceType(type);
        validateStorageMode(storageMode);
        TrainingCategory category = catalogService.requireEnabledCategory(request.getCategoryId());
        validateStoragePayload(storageMode, request);
        tagService.validateEnabledTagIds(request.getTagIds());
        resource.setResourceType(type);
        resource.setStorageMode(storageMode);
        resource.setCategoryId(category.getId());
        resource.setTitle(TrainingText.required(request.getTitle(), "Resource title is required"));
        resource.setSummary(TrainingText.optional(request.getSummary()));
        resource.setDurationSeconds(request.getDurationSeconds());
        applyStoragePayload(resource, storageMode, request);
    }

    private void applyStoragePayload(TrainingResource resource, String storageMode, ResourceRequest request) {
        if (TrainingStorageMode.TEXT.equals(storageMode)) {
            resource.setContent(TrainingText.required(request.getContent(), "Content is required for text resource"));
            resource.setFileResourceId(null);
            resource.setExternalUrl(null);
            return;
        }
        if (TrainingStorageMode.LOCAL_FILE.equals(storageMode)) {
            resource.setContent(null);
            resource.setFileResourceId(request.getFileResourceId());
            resource.setExternalUrl(null);
            return;
        }
        resource.setContent(null);
        resource.setFileResourceId(null);
        resource.setExternalUrl(TrainingText.required(request.getExternalUrl(),
                "External URL is required for external link resource"));
    }

    private void validateStoragePayload(String storageMode, ResourceRequest request) {
        if (TrainingStorageMode.TEXT.equals(storageMode)) {
            TrainingText.required(request.getContent(), "Content is required for text resource");
            return;
        }
        if (TrainingStorageMode.LOCAL_FILE.equals(storageMode)) {
            if (request.getFileResourceId() == null) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "File resource is required for local file resource");
            }
            fileResourceService.getRequiredActiveFile(request.getFileResourceId());
            return;
        }
        String url = TrainingText.required(request.getExternalUrl(),
                "External URL is required for external link resource");
        validateHttpUrl(url);
    }

    private TrainingResource requireResource(Long id) {
        TrainingResource resource = resourceMapper.selectById(id);
        if (resource == null || Integer.valueOf(1).equals(resource.getIsDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Training resource not found");
        }
        return resource;
    }

    private void validateResourceType(String resourceType) {
        if (!Arrays.asList(TrainingResourceType.ARTICLE, TrainingResourceType.VIDEO, TrainingResourceType.PPT)
                .contains(resourceType)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid resource type");
        }
    }

    private void validateStorageMode(String storageMode) {
        if (!Arrays.asList(TrainingStorageMode.TEXT, TrainingStorageMode.LOCAL_FILE,
                TrainingStorageMode.EXTERNAL_LINK).contains(storageMode)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid storage mode");
        }
    }

    private void validateHttpUrl(String url) {
        try {
            URI uri = new URI(url);
            if (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "External URL must be http or https");
            }
            if (!org.springframework.util.StringUtils.hasText(uri.getHost())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "External URL host is required");
            }
        } catch (URISyntaxException ex) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "External URL format is invalid");
        }
    }
}
