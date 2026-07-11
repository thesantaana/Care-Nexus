package com.carenexus.doctor.health;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.carenexus.audit.OperationLogService;
import com.carenexus.auth.CurrentUser;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.doctor.constant.DoctorConstants;
import com.carenexus.doctor.dto.DoctorRequests.AssessmentRequest;
import com.carenexus.doctor.dto.DoctorRequests.FollowUpRequest;
import com.carenexus.doctor.dto.DoctorRequests.InterventionRequest;
import com.carenexus.doctor.entity.FollowUpRecord;
import com.carenexus.doctor.entity.HealthAssessment;
import com.carenexus.doctor.entity.InterventionRecord;
import com.carenexus.doctor.mapper.FollowUpRecordMapper;
import com.carenexus.doctor.mapper.HealthAssessmentMapper;
import com.carenexus.doctor.mapper.InterventionRecordMapper;
import com.carenexus.doctor.support.DoctorAccessPolicy;
import com.carenexus.doctor.support.DoctorAssembler;
import com.carenexus.doctor.vo.DoctorResponses;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HealthCareRecordService {
    private final FollowUpRecordMapper followUpMapper;
    private final InterventionRecordMapper interventionMapper;
    private final HealthAssessmentMapper assessmentMapper;
    private final DoctorAccessPolicy accessPolicy;
    private final OperationLogService operationLogService;
    private final DoctorAssembler assembler;

    public HealthCareRecordService(FollowUpRecordMapper followUpMapper,
            InterventionRecordMapper interventionMapper, HealthAssessmentMapper assessmentMapper,
            DoctorAccessPolicy accessPolicy, OperationLogService operationLogService,
            DoctorAssembler assembler) {
        this.followUpMapper = followUpMapper;
        this.interventionMapper = interventionMapper;
        this.assessmentMapper = assessmentMapper;
        this.accessPolicy = accessPolicy;
        this.operationLogService = operationLogService;
        this.assembler = assembler;
    }

    @Transactional
    public DoctorResponses.FollowUpResponse createFollowUp(Long elderId, FollowUpRequest request) {
        CurrentUser doctor = requireDoctor(elderId);
        FollowUpRecord record = new FollowUpRecord();
        record.setElderId(elderId);
        record.setDoctorId(doctor.getUserId());
        record.setFollowUpMethod(request.getMethod().trim());
        record.setFollowUpResult(trim(request.getResult()));
        applyInitialStatus(record, request.getRecordStatus());
        followUpMapper.insert(record);
        operationLogService.record(doctor, "FOLLOW_UP_CREATE", "FOLLOW_UP", record.getId(), "SUCCESS");
        return assembler.toFollowUp(record);
    }

    @Transactional
    public DoctorResponses.InterventionResponse createIntervention(Long elderId, InterventionRequest request) {
        CurrentUser doctor = requireDoctor(elderId);
        InterventionRecord record = new InterventionRecord();
        record.setElderId(elderId);
        record.setDoctorId(doctor.getUserId());
        record.setInterventionContent(request.getContent().trim());
        applyInitialStatus(record, request.getRecordStatus());
        interventionMapper.insert(record);
        operationLogService.record(doctor, "INTERVENTION_CREATE", "INTERVENTION", record.getId(), "SUCCESS");
        return assembler.toIntervention(record);
    }

    @Transactional
    public DoctorResponses.AssessmentResponse createAssessment(Long elderId, AssessmentRequest request) {
        CurrentUser doctor = requireDoctor(elderId);
        HealthAssessment record = new HealthAssessment();
        record.setElderId(elderId);
        record.setDoctorId(doctor.getUserId());
        record.setRiskLevel(request.getRiskLevel().trim().toUpperCase());
        record.setConclusion(trim(request.getConclusion()));
        record.setSuggestion(trim(request.getSuggestion()));
        applyInitialStatus(record, request.getAssessmentStatus());
        assessmentMapper.insert(record);
        operationLogService.record(doctor, "ASSESSMENT_CREATE", "HEALTH_ASSESSMENT",
                record.getId(), "SUCCESS");
        return assembler.toAssessment(record);
    }

    @Transactional
    public DoctorResponses.FollowUpResponse confirmFollowUp(Long id) {
        FollowUpRecord record = followUpMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Follow-up record not found");
        }
        CurrentUser doctor = requireOwner(record.getDoctorId(), record.getElderId());
        confirm(id, record.getRecordStatus(), followUpMapper, record);
        operationLogService.record(doctor, "FOLLOW_UP_CONFIRM", "FOLLOW_UP", id, "SUCCESS");
        return assembler.toFollowUp(record);
    }

    @Transactional
    public DoctorResponses.InterventionResponse confirmIntervention(Long id) {
        InterventionRecord record = interventionMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Intervention record not found");
        }
        CurrentUser doctor = requireOwner(record.getDoctorId(), record.getElderId());
        if (!DoctorConstants.DRAFT.equals(record.getRecordStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Intervention is already confirmed");
        }
        record.setRecordStatus(DoctorConstants.CONFIRMED);
        record.setConfirmedAt(LocalDateTime.now());
        int updated = interventionMapper.update(record, new UpdateWrapper<InterventionRecord>()
                .eq("id", id).eq("record_status", DoctorConstants.DRAFT));
        requireUpdated(updated);
        operationLogService.record(doctor, "INTERVENTION_CONFIRM", "INTERVENTION", id, "SUCCESS");
        return assembler.toIntervention(record);
    }

    @Transactional
    public DoctorResponses.AssessmentResponse confirmAssessment(Long id) {
        HealthAssessment record = assessmentMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Health assessment not found");
        }
        CurrentUser doctor = requireOwner(record.getDoctorId(), record.getElderId());
        if (!DoctorConstants.DRAFT.equals(record.getAssessmentStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Health assessment is already confirmed");
        }
        record.setAssessmentStatus(DoctorConstants.CONFIRMED);
        record.setConfirmedAt(LocalDateTime.now());
        int updated = assessmentMapper.update(record, new UpdateWrapper<HealthAssessment>()
                .eq("id", id).eq("assessment_status", DoctorConstants.DRAFT));
        requireUpdated(updated);
        operationLogService.record(doctor, "ASSESSMENT_CONFIRM", "HEALTH_ASSESSMENT", id, "SUCCESS");
        return assembler.toAssessment(record);
    }

    private CurrentUser requireDoctor(Long elderId) {
        CurrentUser currentUser = accessPolicy.requireHealthAccess(elderId);
        if (!DoctorConstants.ROLE_DOCTOR.equals(currentUser.getMainRoleCode())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Only doctor can create formal health records");
        }
        return currentUser;
    }

    private CurrentUser requireOwner(Long doctorId, Long elderId) {
        CurrentUser currentUser = requireDoctor(elderId);
        if (!currentUser.getUserId().equals(doctorId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Record belongs to another doctor");
        }
        return currentUser;
    }

    private void applyInitialStatus(FollowUpRecord record, String requested) {
        String status = normalizeInitialStatus(requested);
        record.setRecordStatus(status);
        record.setConfirmedAt(DoctorConstants.CONFIRMED.equals(status) ? LocalDateTime.now() : null);
    }

    private void applyInitialStatus(InterventionRecord record, String requested) {
        String status = normalizeInitialStatus(requested);
        record.setRecordStatus(status);
        record.setConfirmedAt(DoctorConstants.CONFIRMED.equals(status) ? LocalDateTime.now() : null);
    }

    private void applyInitialStatus(HealthAssessment record, String requested) {
        String status = normalizeInitialStatus(requested);
        record.setAssessmentStatus(status);
        record.setConfirmedAt(DoctorConstants.CONFIRMED.equals(status) ? LocalDateTime.now() : null);
    }

    private String normalizeInitialStatus(String requested) {
        String status = requested.trim().toUpperCase();
        if (!DoctorConstants.DRAFT.equals(status) && !DoctorConstants.CONFIRMED.equals(status)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Record status must be DRAFT or CONFIRMED");
        }
        return status;
    }

    private void confirm(Long id, String status, FollowUpRecordMapper mapper, FollowUpRecord record) {
        if (!DoctorConstants.DRAFT.equals(status)) {
            throw new BusinessException(ErrorCode.CONFLICT, "Follow-up is already confirmed");
        }
        record.setRecordStatus(DoctorConstants.CONFIRMED);
        record.setConfirmedAt(LocalDateTime.now());
        int updated = mapper.update(record, new UpdateWrapper<FollowUpRecord>()
                .eq("id", id).eq("record_status", DoctorConstants.DRAFT));
        requireUpdated(updated);
    }

    private void requireUpdated(int updated) {
        if (updated != 1) {
            throw new BusinessException(ErrorCode.CONFLICT, "Record status changed concurrently");
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
