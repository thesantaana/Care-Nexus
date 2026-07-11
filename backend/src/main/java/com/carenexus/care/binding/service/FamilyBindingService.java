package com.carenexus.care.binding.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.carenexus.audit.OperationLogService;
import com.carenexus.auth.CurrentUser;
import com.carenexus.auth.CurrentUserService;
import com.carenexus.care.FamilyElderAccessService;
import com.carenexus.care.constant.CarePermissions;
import com.carenexus.care.constant.CareRoles;
import com.carenexus.care.constant.CareStatuses;
import com.carenexus.care.dto.BindingRequest;
import com.carenexus.care.dto.ReasonRequest;
import com.carenexus.care.entity.ElderFamilyBinding;
import com.carenexus.care.entity.ElderProfile;
import com.carenexus.care.mapper.ElderFamilyBindingMapper;
import com.carenexus.care.mapper.ElderProfileMapper;
import com.carenexus.care.support.CareAccessPolicy;
import com.carenexus.care.support.CareAssembler;
import com.carenexus.care.support.CareText;
import com.carenexus.care.vo.BindingResponse;
import com.carenexus.care.vo.ElderResponse;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FamilyBindingService implements FamilyElderAccessService {

    private static final String VERIFY_TYPE = "BINDING_CODE";

    private final CareAccessPolicy accessPolicy;
    private final CurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;
    private final ElderProfileMapper elderMapper;
    private final ElderFamilyBindingMapper bindingMapper;
    private final OperationLogService operationLogService;
    private final CareAssembler assembler;

    public FamilyBindingService(CareAccessPolicy accessPolicy, CurrentUserService currentUserService,
            PasswordEncoder passwordEncoder, ElderProfileMapper elderMapper,
            ElderFamilyBindingMapper bindingMapper, OperationLogService operationLogService,
            CareAssembler assembler) {
        this.accessPolicy = accessPolicy;
        this.currentUserService = currentUserService;
        this.passwordEncoder = passwordEncoder;
        this.elderMapper = elderMapper;
        this.bindingMapper = bindingMapper;
        this.operationLogService = operationLogService;
        this.assembler = assembler;
    }

    @Transactional
    public BindingResponse bind(BindingRequest request) {
        CurrentUser family = accessPolicy.requirePermission(CarePermissions.BINDING_MANAGE);
        accessPolicy.requireRole(family, CareRoles.FAMILY);
        CurrentUser elderUser = currentUserService.findActiveUserByUsername(
                CareText.required(request.getElderIdentifier(), "Elder identifier is required"));
        if (!CareRoles.ELDER.equals(elderUser.getMainRoleCode())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Target account is not an elder");
        }
        ElderProfile elder = findElderByUserId(elderUser.getUserId());
        if (elder.getBindingCodeHash() == null
                || !passwordEncoder.matches(request.getVerifyCode(), elder.getBindingCodeHash())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Binding verification failed");
        }
        ElderFamilyBinding binding = findBinding(elder.getId(), family.getUserId());
        if (binding != null && CareStatuses.ACTIVE.equals(binding.getBindingStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Binding already exists");
        }
        if (binding == null) {
            binding = new ElderFamilyBinding();
            binding.setElderId(elder.getId());
            binding.setFamilyUserId(family.getUserId());
        }
        binding.setBindingStatus(CareStatuses.ACTIVE);
        binding.setVerifyType(VERIFY_TYPE);
        binding.setVerifiedAt(LocalDateTime.now());
        binding.setCancelReason(null);
        if (binding.getId() == null) {
            bindingMapper.insert(binding);
        } else {
            bindingMapper.updateById(binding);
        }
        operationLogService.record(family, "ELDER_BIND", "ELDER_BINDING", binding.getId(), "SUCCESS");
        return assembler.toBinding(binding, elder);
    }

    @Transactional
    public BindingResponse cancel(Long bindingId, ReasonRequest request) {
        CurrentUser family = accessPolicy.requirePermission(CarePermissions.BINDING_MANAGE);
        ElderFamilyBinding binding = requireBinding(bindingId);
        if (!family.getUserId().equals(binding.getFamilyUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Binding does not belong to current user");
        }
        if (!CareStatuses.ACTIVE.equals(binding.getBindingStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Binding is not active");
        }
        binding.setBindingStatus(CareStatuses.CANCELLED);
        binding.setCancelReason(CareText.required(request.getReason(), "Cancel reason is required"));
        bindingMapper.updateById(binding);
        operationLogService.record(family, "ELDER_BIND_CANCEL", "ELDER_BINDING", binding.getId(), "SUCCESS");
        return assembler.toBinding(binding, elderMapper.selectById(binding.getElderId()));
    }

    public List<ElderResponse> myElders() {
        CurrentUser currentUser = accessPolicy.requireCurrentUser();
        if (CareRoles.ELDER.equals(currentUser.getMainRoleCode())) {
            return Collections.singletonList(assembler.toElder(findElderByUserId(currentUser.getUserId()), null));
        }
        accessPolicy.requireRole(currentUser, CareRoles.FAMILY);
        List<ElderFamilyBinding> bindings = bindingMapper.selectList(new QueryWrapper<ElderFamilyBinding>()
                .eq("family_user_id", currentUser.getUserId())
                .eq("binding_status", CareStatuses.ACTIVE));
        if (bindings.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> elderIds = bindings.stream().map(ElderFamilyBinding::getElderId).collect(Collectors.toList());
        Map<Long, ElderProfile> elderMap = new LinkedHashMap<>();
        for (ElderProfile elder : elderMapper.selectBatchIds(elderIds)) {
            elderMap.put(elder.getId(), elder);
        }
        List<ElderResponse> result = new ArrayList<>();
        for (ElderFamilyBinding binding : bindings) {
            ElderProfile elder = elderMap.get(binding.getElderId());
            if (elder != null) {
                result.add(assembler.toElder(elder, binding.getId()));
            }
        }
        return result;
    }

    public List<Long> accessibleElderIds(CurrentUser currentUser) {
        if (currentUser == null) {
            return Collections.emptyList();
        }
        if (CareRoles.ELDER.equals(currentUser.getMainRoleCode())) {
            ElderProfile elder = findElderByUserId(currentUser.getUserId());
            return Collections.singletonList(elder.getId());
        }
        if (!CareRoles.FAMILY.equals(currentUser.getMainRoleCode())) {
            return Collections.emptyList();
        }
        return bindingMapper.selectList(new QueryWrapper<ElderFamilyBinding>()
                        .eq("family_user_id", currentUser.getUserId())
                        .eq("binding_status", CareStatuses.ACTIVE))
                .stream().map(ElderFamilyBinding::getElderId).collect(Collectors.toList());
    }

    @Override
    public boolean canAccessElder(CurrentUser currentUser, Long elderId) {
        if (currentUser == null || elderId == null) {
            return false;
        }
        if (CareRoles.ELDER.equals(currentUser.getMainRoleCode())) {
            return elderMapper.selectCount(new QueryWrapper<ElderProfile>()
                    .eq("id", elderId).eq("user_id", currentUser.getUserId())) > 0;
        }
        if (CareRoles.FAMILY.equals(currentUser.getMainRoleCode())) {
            return bindingMapper.selectCount(new QueryWrapper<ElderFamilyBinding>()
                    .eq("elder_id", elderId)
                    .eq("family_user_id", currentUser.getUserId())
                    .eq("binding_status", CareStatuses.ACTIVE)) > 0;
        }
        return CareRoles.ADMIN.equals(currentUser.getMainRoleCode())
                || CareRoles.OPERATOR.equals(currentUser.getMainRoleCode());
    }

    @Override
    public boolean canBindElder(CurrentUser currentUser, Long elderId) {
        return currentUser != null && elderId != null && CareRoles.FAMILY.equals(currentUser.getMainRoleCode());
    }

    private ElderProfile findElderByUserId(Long userId) {
        ElderProfile elder = elderMapper.selectOne(new QueryWrapper<ElderProfile>().eq("user_id", userId));
        if (elder == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Elder profile not found");
        }
        return elder;
    }

    private ElderFamilyBinding findBinding(Long elderId, Long familyUserId) {
        return bindingMapper.selectOne(new QueryWrapper<ElderFamilyBinding>()
                .eq("elder_id", elderId).eq("family_user_id", familyUserId));
    }

    private ElderFamilyBinding requireBinding(Long bindingId) {
        ElderFamilyBinding binding = bindingMapper.selectById(bindingId);
        if (binding == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Binding not found");
        }
        return binding;
    }
}
