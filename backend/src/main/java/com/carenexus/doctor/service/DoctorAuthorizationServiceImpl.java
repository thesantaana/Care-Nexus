package com.carenexus.doctor.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carenexus.audit.OperationLogService;
import com.carenexus.auth.CurrentUser;
import com.carenexus.auth.CurrentUserService;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.common.response.PageResponse;
import com.carenexus.doctor.DoctorAuthorizationService;
import com.carenexus.doctor.constant.DoctorConstants;
import com.carenexus.doctor.dto.DoctorRequests.AuthorizationRequest;
import com.carenexus.doctor.entity.DoctorElderAuthorization;
import com.carenexus.doctor.entity.ElderHealthProfile;
import com.carenexus.doctor.mapper.DoctorElderAuthorizationMapper;
import com.carenexus.doctor.mapper.ElderHealthProfileMapper;
import com.carenexus.doctor.support.DoctorAssembler;
import com.carenexus.doctor.vo.DoctorResponses;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoctorAuthorizationServiceImpl implements DoctorAuthorizationService {

    private final DoctorElderAuthorizationMapper authorizationMapper;
    private final ElderHealthProfileMapper elderMapper;
    private final CurrentUserService currentUserService;
    private final OperationLogService operationLogService;
    private final DoctorAssembler assembler;

    public DoctorAuthorizationServiceImpl(DoctorElderAuthorizationMapper authorizationMapper,
            ElderHealthProfileMapper elderMapper, CurrentUserService currentUserService,
            OperationLogService operationLogService, DoctorAssembler assembler) {
        this.authorizationMapper = authorizationMapper;
        this.elderMapper = elderMapper;
        this.currentUserService = currentUserService;
        this.operationLogService = operationLogService;
        this.assembler = assembler;
    }

    @Override
    public boolean canAccessElder(CurrentUser currentUser, Long elderId) {
        if (currentUser == null || elderId == null) {
            return false;
        }
        if (DoctorConstants.ROLE_ADMIN.equals(currentUser.getMainRoleCode())
                || DoctorConstants.ROLE_HEALTH_MANAGER.equals(currentUser.getMainRoleCode())) {
            return elderMapper.selectById(elderId) != null;
        }
        return DoctorConstants.ROLE_DOCTOR.equals(currentUser.getMainRoleCode())
                && authorizationMapper.selectCount(new QueryWrapper<DoctorElderAuthorization>()
                        .eq("doctor_user_id", currentUser.getUserId())
                        .eq("elder_id", elderId).eq("auth_status", DoctorConstants.ACTIVE)) > 0;
    }

    @Transactional
    public DoctorResponses.AuthorizationResponse maintain(CurrentUser operator, AuthorizationRequest request) {
        CurrentUser doctor = currentUserService.loadActiveUser(request.getDoctorUserId());
        if (!DoctorConstants.ROLE_DOCTOR.equals(doctor.getMainRoleCode())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Target user is not a doctor");
        }
        if (elderMapper.selectById(request.getElderId()) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Elder profile not found");
        }
        String targetStatus = request.getAuthStatus().trim().toUpperCase();
        if (!DoctorConstants.ACTIVE.equals(targetStatus) && !DoctorConstants.CANCELLED.equals(targetStatus)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Unsupported authorization status");
        }
        DoctorElderAuthorization authorization = find(request.getDoctorUserId(), request.getElderId());
        if (authorization != null && targetStatus.equals(authorization.getAuthStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Authorization already has target status");
        }
        if (authorization == null) {
            if (DoctorConstants.CANCELLED.equals(targetStatus)) {
                throw new BusinessException(ErrorCode.CONFLICT, "Authorization does not exist");
            }
            authorization = new DoctorElderAuthorization();
            authorization.setDoctorUserId(request.getDoctorUserId());
            authorization.setElderId(request.getElderId());
        }
        applyStatus(authorization, targetStatus, operator.getUserId());
        if (authorization.getId() == null) {
            authorizationMapper.insert(authorization);
        } else {
            authorizationMapper.updateById(authorization);
        }
        operationLogService.record(operator, "DOCTOR_ELDER_AUTH_" + targetStatus,
                "DOCTOR_ELDER_AUTHORIZATION", authorization.getId(), "SUCCESS");
        return assembler.toAuthorization(authorization);
    }

    public PageResponse<DoctorResponses.ElderProfileResponse> doctorElders(CurrentUser requester,
            Long doctorUserId, String keyword, int pageNo, int pageSize) {
        if (DoctorConstants.ROLE_DOCTOR.equals(requester.getMainRoleCode())
                && !requester.getUserId().equals(doctorUserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Doctor can only view own authorizations");
        }
        List<Long> elderIds = authorizationMapper.selectList(new QueryWrapper<DoctorElderAuthorization>()
                        .eq("doctor_user_id", doctorUserId).eq("auth_status", DoctorConstants.ACTIVE))
                .stream().map(DoctorElderAuthorization::getElderId).collect(Collectors.toList());
        return pageElders(elderIds, keyword, pageNo, pageSize);
    }

    public PageResponse<DoctorResponses.AuthorizationResponse> elderDoctors(Long elderId, int pageNo, int pageSize) {
        Page<DoctorElderAuthorization> page = authorizationMapper.selectPage(new Page<>(pageNo, pageSize),
                new QueryWrapper<DoctorElderAuthorization>().eq("elder_id", elderId)
                        .eq("auth_status", DoctorConstants.ACTIVE).orderByDesc("authorized_at"));
        return PageResponse.from(page, page.getRecords().stream()
                .map(assembler::toAuthorization).collect(Collectors.toList()));
    }

    public PageResponse<DoctorResponses.ElderProfileResponse> managedElders(String keyword,
            int pageNo, int pageSize) {
        QueryWrapper<ElderHealthProfile> wrapper = new QueryWrapper<ElderHealthProfile>()
                .eq("is_deleted", 0).orderByDesc("id");
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like("elder_name", keyword.trim());
        }
        Page<ElderHealthProfile> page = elderMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        return PageResponse.from(page, page.getRecords().stream().map(assembler::toElder)
                .collect(Collectors.toList()));
    }

    private PageResponse<DoctorResponses.ElderProfileResponse> pageElders(List<Long> elderIds,
            String keyword, int pageNo, int pageSize) {
        if (elderIds.isEmpty()) {
            return PageResponse.empty(pageNo, pageSize);
        }
        QueryWrapper<ElderHealthProfile> wrapper = new QueryWrapper<ElderHealthProfile>()
                .in("id", elderIds).eq("is_deleted", 0).orderByDesc("id");
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like("elder_name", keyword.trim());
        }
        Page<ElderHealthProfile> page = elderMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        return PageResponse.from(page, page.getRecords().stream().map(assembler::toElder)
                .collect(Collectors.toList()));
    }

    private DoctorElderAuthorization find(Long doctorId, Long elderId) {
        return authorizationMapper.selectOne(new QueryWrapper<DoctorElderAuthorization>()
                .eq("doctor_user_id", doctorId).eq("elder_id", elderId));
    }

    private void applyStatus(DoctorElderAuthorization authorization, String status, Long operatorId) {
        LocalDateTime now = LocalDateTime.now();
        authorization.setAuthStatus(status);
        if (DoctorConstants.ACTIVE.equals(status)) {
            authorization.setAuthorizedBy(operatorId);
            authorization.setAuthorizedAt(now);
            authorization.setCancelledBy(null);
            authorization.setCancelledAt(null);
        } else {
            authorization.setCancelledBy(operatorId);
            authorization.setCancelledAt(now);
        }
    }
}
