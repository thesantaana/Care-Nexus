package com.carenexus.training.catalog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.carenexus.auth.CurrentUser;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.training.constant.EnableStatus;
import com.carenexus.training.dto.CategoryRequest;
import com.carenexus.training.dto.StatusRequest;
import com.carenexus.training.dto.TagRequest;
import com.carenexus.training.entity.TrainingCategory;
import com.carenexus.training.entity.TrainingTag;
import com.carenexus.training.mapper.TrainingCategoryMapper;
import com.carenexus.training.mapper.TrainingTagMapper;
import com.carenexus.training.resource.support.TrainingResourceAccessPolicy;
import com.carenexus.training.support.TrainingText;
import com.carenexus.training.vo.CategoryResponse;
import com.carenexus.training.vo.TagResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TrainingCatalogService {

    private static final int ACTIVE_FLAG = 0;

    private final TrainingResourceAccessPolicy accessPolicy;
    private final TrainingCategoryMapper categoryMapper;
    private final TrainingTagMapper tagMapper;

    public TrainingCatalogService(TrainingResourceAccessPolicy accessPolicy, TrainingCategoryMapper categoryMapper,
            TrainingTagMapper tagMapper) {
        this.accessPolicy = accessPolicy;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
    }

    public List<CategoryResponse> listCategories(String status) {
        CurrentUser currentUser = accessPolicy.requireViewOrManage();
        QueryWrapper<TrainingCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", ACTIVE_FLAG);
        String queryStatus = accessPolicy.hasManage(currentUser) ? TrainingText.optional(status)
                : EnableStatus.ENABLED;
        if (StringUtils.hasText(queryStatus)) {
            validateEnableStatus(queryStatus);
            wrapper.eq("category_status", queryStatus);
        }
        wrapper.orderByAsc("sort_no").orderByAsc("id");
        return categoryMapper.selectList(wrapper).stream().map(this::toCategoryResponse).collect(Collectors.toList());
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        accessPolicy.requireManage();
        String name = TrainingText.required(request.getCategoryName(), "Category name is required");
        ensureUniqueCategoryName(name, null);
        TrainingCategory category = new TrainingCategory();
        category.setCategoryName(name);
        category.setSortNo(request.getSortNo());
        category.setCategoryStatus(EnableStatus.ENABLED);
        category.setIsDeleted(ACTIVE_FLAG);
        categoryMapper.insert(category);
        return toCategoryResponse(category);
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        accessPolicy.requireManage();
        TrainingCategory category = requireCategory(id);
        String name = TrainingText.required(request.getCategoryName(), "Category name is required");
        ensureUniqueCategoryName(name, id);
        category.setCategoryName(name);
        category.setSortNo(request.getSortNo());
        categoryMapper.updateById(category);
        return toCategoryResponse(category);
    }

    public CategoryResponse updateCategoryStatus(Long id, StatusRequest request) {
        accessPolicy.requireManage();
        TrainingCategory category = requireCategory(id);
        String status = TrainingText.required(request.getStatus(), "Status is required");
        validateEnableStatus(status);
        category.setCategoryStatus(status);
        categoryMapper.updateById(category);
        return toCategoryResponse(category);
    }

    public List<TagResponse> listTags(String status) {
        CurrentUser currentUser = accessPolicy.requireViewOrManage();
        QueryWrapper<TrainingTag> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", ACTIVE_FLAG);
        String queryStatus = accessPolicy.hasManage(currentUser) ? TrainingText.optional(status)
                : EnableStatus.ENABLED;
        if (StringUtils.hasText(queryStatus)) {
            validateEnableStatus(queryStatus);
            wrapper.eq("tag_status", queryStatus);
        }
        wrapper.orderByAsc("id");
        return tagMapper.selectList(wrapper).stream().map(this::toTagResponse).collect(Collectors.toList());
    }

    public TagResponse createTag(TagRequest request) {
        accessPolicy.requireManage();
        String name = TrainingText.required(request.getTagName(), "Tag name is required");
        ensureUniqueTagName(name, null);
        TrainingTag tag = new TrainingTag();
        tag.setTagName(name);
        tag.setTagStatus(EnableStatus.ENABLED);
        tag.setIsDeleted(ACTIVE_FLAG);
        tagMapper.insert(tag);
        return toTagResponse(tag);
    }

    public TagResponse updateTag(Long id, TagRequest request) {
        accessPolicy.requireManage();
        TrainingTag tag = requireTag(id);
        String name = TrainingText.required(request.getTagName(), "Tag name is required");
        ensureUniqueTagName(name, id);
        tag.setTagName(name);
        tagMapper.updateById(tag);
        return toTagResponse(tag);
    }

    public TagResponse updateTagStatus(Long id, StatusRequest request) {
        accessPolicy.requireManage();
        TrainingTag tag = requireTag(id);
        String status = TrainingText.required(request.getStatus(), "Status is required");
        validateEnableStatus(status);
        tag.setTagStatus(status);
        tagMapper.updateById(tag);
        return toTagResponse(tag);
    }

    public TrainingCategory requireEnabledCategory(Long id) {
        TrainingCategory category = requireCategory(id);
        if (!EnableStatus.ENABLED.equals(category.getCategoryStatus())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Training category not found");
        }
        return category;
    }

    public TrainingTag requireEnabledTag(Long id) {
        TrainingTag tag = requireTag(id);
        if (!EnableStatus.ENABLED.equals(tag.getTagStatus())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Training tag not found");
        }
        return tag;
    }

    private TrainingCategory requireCategory(Long id) {
        TrainingCategory category = categoryMapper.selectById(id);
        if (category == null || Integer.valueOf(1).equals(category.getIsDeleted())) {
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

    private void validateEnableStatus(String status) {
        if (!Arrays.asList(EnableStatus.ENABLED, EnableStatus.DISABLED).contains(status)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid status");
        }
    }

    private CategoryResponse toCategoryResponse(TrainingCategory category) {
        return new CategoryResponse(category.getId(), category.getCategoryName(), category.getSortNo(),
                category.getCategoryStatus());
    }

    private TagResponse toTagResponse(TrainingTag tag) {
        return new TagResponse(tag.getId(), tag.getTagName(), tag.getTagStatus());
    }
}
