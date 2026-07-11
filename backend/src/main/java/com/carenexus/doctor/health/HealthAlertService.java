package com.carenexus.doctor.health;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carenexus.audit.OperationLogService;
import com.carenexus.auth.CurrentUser;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.common.response.PageResponse;
import com.carenexus.doctor.constant.DoctorConstants;
import com.carenexus.doctor.dto.DoctorRequests.AlertCreateRequest;
import com.carenexus.doctor.dto.DoctorRequests.AlertStatusRequest;
import com.carenexus.doctor.entity.HealthAlert;
import com.carenexus.doctor.entity.HealthRecord;
import com.carenexus.doctor.mapper.HealthAlertMapper;
import com.carenexus.doctor.support.DoctorAccessPolicy;
import com.carenexus.doctor.support.DoctorAssembler;
import com.carenexus.doctor.vo.DoctorResponses;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HealthAlertService {
    private final HealthAlertMapper alertMapper;
    private final HealthThresholdProperties thresholds;
    private final DoctorAccessPolicy accessPolicy;
    private final OperationLogService operationLogService;
    private final DoctorAssembler assembler;

    public HealthAlertService(HealthAlertMapper alertMapper, HealthThresholdProperties thresholds,
            DoctorAccessPolicy accessPolicy, OperationLogService operationLogService,
            DoctorAssembler assembler) {
        this.alertMapper = alertMapper;
        this.thresholds = thresholds;
        this.accessPolicy = accessPolicy;
        this.operationLogService = operationLogService;
        this.assembler = assembler;
    }

    public List<HealthAlert> createThresholdAlerts(HealthRecord record) {
        List<String> warnings = detect(record);
        List<HealthAlert> result = new ArrayList<>();
        for (String warning : warnings) {
            HealthAlert alert = newAlert(record.getElderId(), record.getId(),
                    DoctorConstants.SOURCE_THRESHOLD, DoctorConstants.LEVEL_WARNING, warning);
            alertMapper.insert(alert);
            result.add(alert);
        }
        return result;
    }

    @Transactional
    public DoctorResponses.HealthAlertResponse createManual(Long elderId, AlertCreateRequest request) {
        CurrentUser currentUser = accessPolicy.requireHealthAccess(elderId);
        HealthAlert alert = newAlert(elderId, null, DoctorConstants.SOURCE_MANUAL,
                textOrDefault(request.getAlertLevel(), DoctorConstants.LEVEL_WARNING), request.getAlertContent());
        alertMapper.insert(alert);
        operationLogService.record(currentUser, "HEALTH_ALERT_CREATE", "HEALTH_ALERT", alert.getId(), "SUCCESS");
        return assembler.toAlert(alert);
    }

    public PageResponse<DoctorResponses.HealthAlertResponse> page(Long elderId, String status,
            int pageNo, int pageSize) {
        accessPolicy.requireElderAccess(elderId);
        QueryWrapper<HealthAlert> wrapper = new QueryWrapper<HealthAlert>()
                .eq("elder_id", elderId).orderByDesc("created_at");
        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq("alert_status", status.trim().toUpperCase());
        }
        Page<HealthAlert> page = alertMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        return PageResponse.from(page, page.getRecords().stream().map(assembler::toAlert)
                .collect(Collectors.toList()));
    }

    @Transactional
    public DoctorResponses.HealthAlertResponse updateStatus(Long id, AlertStatusRequest request) {
        HealthAlert alert = requireAlert(id);
        CurrentUser currentUser = accessPolicy.requireHealthAccess(alert.getElderId());
        String target = request.getAlertStatus().trim().toUpperCase();
        String expected = expectedStatus(target);
        if (!expected.equals(alert.getAlertStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Health alert status does not allow this operation");
        }
        alert.setAlertStatus(target);
        alert.setHandledBy(currentUser.getUserId());
        alert.setHandledComment(request.getComment().trim());
        alert.setHandledAt(LocalDateTime.now());
        int updated = alertMapper.update(alert, new UpdateWrapper<HealthAlert>()
                .eq("id", id).eq("alert_status", expected));
        if (updated != 1) {
            throw new BusinessException(ErrorCode.CONFLICT, "Health alert status changed concurrently");
        }
        operationLogService.record(currentUser, "HEALTH_ALERT_" + target,
                "HEALTH_ALERT", id, "SUCCESS");
        return assembler.toAlert(alert);
    }

    private List<String> detect(HealthRecord record) {
        List<String> warnings = new ArrayList<>();
        addRangeWarning(warnings, "收缩压", record.getSystolicPressure(),
                thresholds.getSystolicLow(), thresholds.getSystolicHigh());
        addRangeWarning(warnings, "舒张压", record.getDiastolicPressure(),
                thresholds.getDiastolicLow(), thresholds.getDiastolicHigh());
        addRangeWarning(warnings, "心率", record.getHeartRate(),
                thresholds.getHeartRateLow(), thresholds.getHeartRateHigh());
        if (record.getBloodGlucose() != null && (record.getBloodGlucose().compareTo(thresholds.getGlucoseLow()) < 0
                || record.getBloodGlucose().compareTo(thresholds.getGlucoseHigh()) > 0)) {
            warnings.add("血糖超出基础提示范围");
        }
        if (record.getBodyTemperature() != null
                && (record.getBodyTemperature().compareTo(thresholds.getTemperatureLow()) < 0
                || record.getBodyTemperature().compareTo(thresholds.getTemperatureHigh()) > 0)) {
            warnings.add("体温超出基础提示范围");
        }
        return warnings;
    }

    private void addRangeWarning(List<String> warnings, String name, Integer value, int low, int high) {
        if (value != null && (value < low || value > high)) {
            warnings.add(name + "超出基础提示范围");
        }
    }

    private HealthAlert newAlert(Long elderId, Long recordId, String source, String level, String content) {
        HealthAlert alert = new HealthAlert();
        alert.setElderId(elderId);
        alert.setHealthRecordId(recordId);
        alert.setAlertSource(source);
        alert.setAlertLevel(level);
        alert.setAlertStatus(DoctorConstants.PENDING);
        alert.setAlertContent(content.trim());
        return alert;
    }

    private String expectedStatus(String target) {
        if (DoctorConstants.PROCESSING.equals(target)) {
            return DoctorConstants.PENDING;
        }
        if (DoctorConstants.CLOSED.equals(target)) {
            return DoctorConstants.PROCESSING;
        }
        throw new BusinessException(ErrorCode.BAD_REQUEST, "Unsupported health alert status");
    }

    private HealthAlert requireAlert(Long id) {
        HealthAlert alert = alertMapper.selectById(id);
        if (alert == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Health alert not found");
        }
        return alert;
    }

    private String textOrDefault(String value, String defaultValue) {
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim().toUpperCase();
    }
}
