package com.carenexus.doctor.service;

import com.carenexus.auth.CurrentUser;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.common.response.PageResponse;
import com.carenexus.doctor.constant.DoctorConstants;
import com.carenexus.doctor.entity.ElderHealthProfile;
import com.carenexus.doctor.health.HealthRecordService;
import com.carenexus.doctor.mapper.ElderHealthProfileMapper;
import com.carenexus.doctor.support.DoctorAccessPolicy;
import com.carenexus.doctor.support.DoctorAssembler;
import com.carenexus.doctor.vo.DoctorResponses;
import org.springframework.stereotype.Service;

@Service
public class DoctorElderService {
    private final DoctorAccessPolicy accessPolicy;
    private final DoctorAuthorizationServiceImpl authorizationService;
    private final ElderHealthProfileMapper elderMapper;
    private final HealthRecordService healthRecordService;
    private final DoctorAssembler assembler;

    public DoctorElderService(DoctorAccessPolicy accessPolicy,
            DoctorAuthorizationServiceImpl authorizationService, ElderHealthProfileMapper elderMapper,
            HealthRecordService healthRecordService, DoctorAssembler assembler) {
        this.accessPolicy = accessPolicy;
        this.authorizationService = authorizationService;
        this.elderMapper = elderMapper;
        this.healthRecordService = healthRecordService;
        this.assembler = assembler;
    }

    public PageResponse<DoctorResponses.ElderProfileResponse> myElders(String keyword,
            int pageNo, int pageSize) {
        CurrentUser currentUser = accessPolicy.requireView();
        if (DoctorConstants.ROLE_DOCTOR.equals(currentUser.getMainRoleCode())) {
            return authorizationService.doctorElders(currentUser, currentUser.getUserId(),
                    keyword, pageNo, pageSize);
        }
        if (DoctorConstants.ROLE_HEALTH_MANAGER.equals(currentUser.getMainRoleCode())
                || DoctorConstants.ROLE_ADMIN.equals(currentUser.getMainRoleCode())) {
            return authorizationService.managedElders(keyword, pageNo, pageSize);
        }
        throw new BusinessException(ErrorCode.FORBIDDEN, "Role cannot view doctor elder list");
    }

    public DoctorResponses.ElderProfileResponse detail(Long elderId) {
        accessPolicy.requireElderAccess(elderId);
        ElderHealthProfile elder = elderMapper.selectById(elderId);
        if (elder == null || Integer.valueOf(1).equals(elder.getIsDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Elder profile not found");
        }
        DoctorResponses.ElderProfileResponse response = assembler.toElder(elder);
        response.latestHealthRecord = healthRecordService.latest(elderId);
        return response;
    }
}
