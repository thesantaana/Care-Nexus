package com.carenexus.care.address.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.carenexus.audit.OperationLogService;
import com.carenexus.auth.CurrentUser;
import com.carenexus.care.FamilyElderAccessService;
import com.carenexus.care.constant.CarePermissions;
import com.carenexus.care.constant.CareStatuses;
import com.carenexus.care.dto.AddressDefaultRequest;
import com.carenexus.care.dto.AddressRequest;
import com.carenexus.care.dto.AddressUpdateRequest;
import com.carenexus.care.dto.ReasonRequest;
import com.carenexus.care.entity.CareAddress;
import com.carenexus.care.mapper.CareAddressMapper;
import com.carenexus.care.support.CareAccessPolicy;
import com.carenexus.care.support.CareAssembler;
import com.carenexus.care.support.CareText;
import com.carenexus.care.support.ContactProtectionService;
import com.carenexus.care.vo.AddressResponse;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CareAddressService {

    private final CareAccessPolicy accessPolicy;
    private final FamilyElderAccessService elderAccessService;
    private final CareAddressMapper addressMapper;
    private final ContactProtectionService contactProtectionService;
    private final OperationLogService operationLogService;
    private final CareAssembler assembler;

    public CareAddressService(CareAccessPolicy accessPolicy, FamilyElderAccessService elderAccessService,
            CareAddressMapper addressMapper, ContactProtectionService contactProtectionService,
            OperationLogService operationLogService, CareAssembler assembler) {
        this.accessPolicy = accessPolicy;
        this.elderAccessService = elderAccessService;
        this.addressMapper = addressMapper;
        this.contactProtectionService = contactProtectionService;
        this.operationLogService = operationLogService;
        this.assembler = assembler;
    }

    public List<AddressResponse> list(Long elderId) {
        CurrentUser currentUser = accessPolicy.requirePermission(CarePermissions.ADDRESS_MANAGE);
        requireElderAccess(currentUser, elderId);
        return addressMapper.selectList(new QueryWrapper<CareAddress>()
                        .eq("owner_user_id", currentUser.getUserId())
                        .eq("elder_id", elderId)
                        .eq("is_deleted", 0)
                        .orderByDesc("is_default").orderByDesc("id"))
                .stream().map(assembler::toAddress).collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse create(AddressRequest request) {
        CurrentUser currentUser = accessPolicy.requirePermission(CarePermissions.ADDRESS_MANAGE);
        requireElderAccess(currentUser, request.getElderId());
        CareAddress address = new CareAddress();
        address.setOwnerUserId(currentUser.getUserId());
        address.setElderId(request.getElderId());
        apply(address, request.getContactName(), request.getMobile(), request.getAddressDetail());
        address.setAddressStatus(CareStatuses.ACTIVE);
        address.setIsDeleted(0);
        boolean makeDefault = Boolean.TRUE.equals(request.getDefaultAddress())
                || activeAddressCount(currentUser.getUserId(), request.getElderId()) == 0;
        if (makeDefault) {
            clearDefault(currentUser.getUserId(), request.getElderId());
        }
        address.setIsDefault(makeDefault ? 1 : 0);
        addressMapper.insert(address);
        operationLogService.record(currentUser, "CARE_ADDRESS_CREATE", "CARE_ADDRESS", address.getId(), "SUCCESS");
        return assembler.toAddress(address);
    }

    @Transactional
    public AddressResponse update(Long id, AddressUpdateRequest request) {
        CurrentUser currentUser = accessPolicy.requirePermission(CarePermissions.ADDRESS_MANAGE);
        CareAddress address = requireOwnedAddress(currentUser, id);
        apply(address, request.getContactName(), request.getMobile(), request.getAddressDetail());
        addressMapper.updateById(address);
        operationLogService.record(currentUser, "CARE_ADDRESS_UPDATE", "CARE_ADDRESS", address.getId(), "SUCCESS");
        return assembler.toAddress(address);
    }

    @Transactional
    public AddressResponse disable(Long id, ReasonRequest request) {
        CurrentUser currentUser = accessPolicy.requirePermission(CarePermissions.ADDRESS_MANAGE);
        CareAddress address = requireOwnedAddress(currentUser, id);
        if (!CareStatuses.ACTIVE.equals(address.getAddressStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Address is already disabled");
        }
        CareText.required(request.getReason(), "Disable reason is required");
        address.setAddressStatus(CareStatuses.DISABLED);
        address.setIsDefault(0);
        addressMapper.updateById(address);
        operationLogService.record(currentUser, "CARE_ADDRESS_DISABLE", "CARE_ADDRESS", address.getId(), "SUCCESS");
        return assembler.toAddress(address);
    }

    @Transactional
    public AddressResponse setDefault(Long id, AddressDefaultRequest request) {
        CurrentUser currentUser = accessPolicy.requirePermission(CarePermissions.ADDRESS_MANAGE);
        CareAddress address = requireOwnedAddress(currentUser, id);
        if (!request.getElderId().equals(address.getElderId())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Address elder does not match");
        }
        if (!CareStatuses.ACTIVE.equals(address.getAddressStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Disabled address cannot be default");
        }
        clearDefault(currentUser.getUserId(), address.getElderId());
        address.setIsDefault(1);
        addressMapper.updateById(address);
        operationLogService.record(currentUser, "CARE_ADDRESS_DEFAULT", "CARE_ADDRESS", address.getId(), "SUCCESS");
        return assembler.toAddress(address);
    }

    public CareAddress requireUsableAddress(CurrentUser currentUser, Long addressId, Long elderId) {
        CareAddress address = requireOwnedAddress(currentUser, addressId);
        if (!elderId.equals(address.getElderId()) || !CareStatuses.ACTIVE.equals(address.getAddressStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Address is not available for this elder");
        }
        return address;
    }

    private CareAddress requireOwnedAddress(CurrentUser currentUser, Long id) {
        CareAddress address = addressMapper.selectById(id);
        if (address == null || Integer.valueOf(1).equals(address.getIsDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Address not found");
        }
        if (!currentUser.getUserId().equals(address.getOwnerUserId())
                || !elderAccessService.canAccessElder(currentUser, address.getElderId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Address does not belong to current user");
        }
        return address;
    }

    private void requireElderAccess(CurrentUser currentUser, Long elderId) {
        if (!elderAccessService.canAccessElder(currentUser, elderId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Elder is outside current user scope");
        }
    }

    private void apply(CareAddress address, String contactName, String mobile, String detail) {
        address.setContactName(CareText.required(contactName, "Contact name is required"));
        address.setContactMobileCipherText(contactProtectionService.protect(mobile));
        address.setContactMobileLast4(contactProtectionService.last4(mobile));
        address.setAddressDetail(CareText.required(detail, "Address detail is required"));
    }

    private long activeAddressCount(Long ownerId, Long elderId) {
        return addressMapper.selectCount(new QueryWrapper<CareAddress>()
                .eq("owner_user_id", ownerId).eq("elder_id", elderId)
                .eq("address_status", CareStatuses.ACTIVE).eq("is_deleted", 0));
    }

    private void clearDefault(Long ownerId, Long elderId) {
        addressMapper.update(null, new UpdateWrapper<CareAddress>()
                .eq("owner_user_id", ownerId).eq("elder_id", elderId)
                .eq("is_default", 1).set("is_default", 0));
    }
}
