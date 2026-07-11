package com.carenexus.care.catalog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carenexus.audit.OperationLogService;
import com.carenexus.auth.CurrentUser;
import com.carenexus.care.constant.CarePermissions;
import com.carenexus.care.constant.CareStatuses;
import com.carenexus.care.dto.ServiceItemRequest;
import com.carenexus.care.dto.ServiceStatusRequest;
import com.carenexus.care.entity.CareServiceItem;
import com.carenexus.care.mapper.CareServiceItemMapper;
import com.carenexus.care.support.CareAccessPolicy;
import com.carenexus.care.support.CareAssembler;
import com.carenexus.care.support.CareText;
import com.carenexus.care.vo.ServiceItemResponse;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.common.response.PageResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CareServiceItemService {

    private final CareAccessPolicy accessPolicy;
    private final CareServiceItemMapper itemMapper;
    private final OperationLogService operationLogService;
    private final CareAssembler assembler;

    public CareServiceItemService(CareAccessPolicy accessPolicy, CareServiceItemMapper itemMapper,
            OperationLogService operationLogService, CareAssembler assembler) {
        this.accessPolicy = accessPolicy;
        this.itemMapper = itemMapper;
        this.operationLogService = operationLogService;
        this.assembler = assembler;
    }

    public PageResponse<ServiceItemResponse> pageMobile(String keyword, String category, int pageNo, int pageSize) {
        accessPolicy.requirePermission(CarePermissions.SERVICE_VIEW);
        return page(keyword, category, CareStatuses.ENABLED, pageNo, pageSize);
    }

    public PageResponse<ServiceItemResponse> pageAdmin(String keyword, String category, String status,
            int pageNo, int pageSize) {
        accessPolicy.requirePermission(CarePermissions.SERVICE_MANAGE);
        return page(keyword, category, status, pageNo, pageSize);
    }

    public ServiceItemResponse mobileDetail(Long id) {
        accessPolicy.requirePermission(CarePermissions.SERVICE_VIEW);
        CareServiceItem item = requireItem(id);
        if (!CareStatuses.ENABLED.equals(item.getServiceStatus())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Service item not found");
        }
        return assembler.toServiceItem(item);
    }

    @Transactional
    public ServiceItemResponse create(ServiceItemRequest request) {
        CurrentUser operator = accessPolicy.requirePermission(CarePermissions.SERVICE_MANAGE);
        String name = CareText.required(request.getServiceName(), "Service name is required");
        ensureNameUnique(name, null);
        CareServiceItem item = new CareServiceItem();
        apply(item, request, name);
        item.setServiceStatus(CareStatuses.ENABLED);
        item.setIsDeleted(0);
        itemMapper.insert(item);
        operationLogService.record(operator, "CARE_SERVICE_CREATE", "CARE_SERVICE_ITEM", item.getId(), "SUCCESS");
        return assembler.toServiceItem(item);
    }

    @Transactional
    public ServiceItemResponse update(Long id, ServiceItemRequest request) {
        CurrentUser operator = accessPolicy.requirePermission(CarePermissions.SERVICE_MANAGE);
        CareServiceItem item = requireItem(id);
        String name = CareText.required(request.getServiceName(), "Service name is required");
        ensureNameUnique(name, id);
        apply(item, request, name);
        itemMapper.updateById(item);
        operationLogService.record(operator, "CARE_SERVICE_UPDATE", "CARE_SERVICE_ITEM", item.getId(), "SUCCESS");
        return assembler.toServiceItem(item);
    }

    @Transactional
    public ServiceItemResponse updateStatus(Long id, ServiceStatusRequest request) {
        CurrentUser operator = accessPolicy.requirePermission(CarePermissions.SERVICE_MANAGE);
        CareServiceItem item = requireItem(id);
        String status = CareText.required(request.getServiceStatus(), "Service status is required").toUpperCase();
        if (!CareStatuses.ENABLED.equals(status) && !CareStatuses.DISABLED.equals(status)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Unsupported service status");
        }
        item.setServiceStatus(status);
        itemMapper.updateById(item);
        operationLogService.record(operator, "CARE_SERVICE_STATUS", "CARE_SERVICE_ITEM", item.getId(), "SUCCESS");
        return assembler.toServiceItem(item);
    }

    public CareServiceItem requireEnabled(Long id) {
        CareServiceItem item = requireItem(id);
        if (!CareStatuses.ENABLED.equals(item.getServiceStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Service item is disabled");
        }
        return item;
    }

    private PageResponse<ServiceItemResponse> page(String keyword, String category, String status,
            int pageNo, int pageSize) {
        QueryWrapper<CareServiceItem> wrapper = new QueryWrapper<CareServiceItem>()
                .eq("is_deleted", 0)
                .orderByDesc("id");
        if (StringUtils.hasText(keyword)) {
            wrapper.like("service_name", keyword.trim());
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq("service_category", category.trim());
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq("service_status", status.trim().toUpperCase());
        }
        Page<CareServiceItem> page = itemMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        List<ServiceItemResponse> records = page.getRecords().stream()
                .map(assembler::toServiceItem).collect(Collectors.toList());
        return PageResponse.from(page, records);
    }

    private CareServiceItem requireItem(Long id) {
        CareServiceItem item = itemMapper.selectById(id);
        if (item == null || Integer.valueOf(1).equals(item.getIsDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Service item not found");
        }
        return item;
    }

    private void ensureNameUnique(String name, Long excludedId) {
        QueryWrapper<CareServiceItem> wrapper = new QueryWrapper<CareServiceItem>()
                .eq("service_name", name).eq("is_deleted", 0);
        if (excludedId != null) {
            wrapper.ne("id", excludedId);
        }
        if (itemMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "Service name already exists");
        }
    }

    private void apply(CareServiceItem item, ServiceItemRequest request, String name) {
        item.setServiceName(name);
        item.setServiceCategory(CareText.optional(request.getCategory()));
        item.setDescription(CareText.optional(request.getDescription()));
    }
}
