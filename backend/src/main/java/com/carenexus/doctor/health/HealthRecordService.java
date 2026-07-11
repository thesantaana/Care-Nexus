package com.carenexus.doctor.health;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carenexus.audit.OperationLogService;
import com.carenexus.auth.CurrentUser;
import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.carenexus.common.response.PageResponse;
import com.carenexus.doctor.dto.DoctorRequests.HealthRecordRequest;
import com.carenexus.doctor.entity.HealthAlert;
import com.carenexus.doctor.entity.HealthRecord;
import com.carenexus.doctor.mapper.HealthRecordMapper;
import com.carenexus.doctor.support.DoctorAccessPolicy;
import com.carenexus.doctor.support.DoctorAssembler;
import com.carenexus.doctor.vo.DoctorResponses;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HealthRecordService {
    private final HealthRecordMapper recordMapper;
    private final HealthAlertService alertService;
    private final DoctorAccessPolicy accessPolicy;
    private final OperationLogService operationLogService;
    private final DoctorAssembler assembler;

    public HealthRecordService(HealthRecordMapper recordMapper, HealthAlertService alertService,
            DoctorAccessPolicy accessPolicy, OperationLogService operationLogService,
            DoctorAssembler assembler) {
        this.recordMapper = recordMapper;
        this.alertService = alertService;
        this.accessPolicy = accessPolicy;
        this.operationLogService = operationLogService;
        this.assembler = assembler;
    }

    @Transactional
    public DoctorResponses.HealthRecordResponse create(Long elderId, HealthRecordRequest request) {
        CurrentUser currentUser = accessPolicy.requireHealthAccess(elderId);
        requireIndicator(request);
        HealthRecord record = new HealthRecord();
        record.setElderId(elderId);
        record.setRecorderId(currentUser.getUserId());
        record.setSystolicPressure(request.getSystolicPressure());
        record.setDiastolicPressure(request.getDiastolicPressure());
        record.setBloodGlucose(request.getBloodGlucose());
        record.setHeartRate(request.getHeartRate());
        record.setBodyTemperature(request.getBodyTemperature());
        record.setRecordTime(request.getRecordTime());
        record.setRemark(trim(request.getRemark()));
        recordMapper.insert(record);
        List<HealthAlert> alerts = alertService.createThresholdAlerts(record);
        operationLogService.record(currentUser, "HEALTH_RECORD_CREATE", "HEALTH_RECORD",
                record.getId(), "SUCCESS");
        DoctorResponses.HealthRecordResponse response = assembler.toRecord(record);
        response.alerts = alerts.stream().map(assembler::toAlert).collect(Collectors.toList());
        return response;
    }

    public PageResponse<DoctorResponses.HealthRecordResponse> page(Long elderId,
            LocalDateTime startTime, LocalDateTime endTime, int pageNo, int pageSize) {
        accessPolicy.requireElderAccess(elderId);
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Start time cannot be after end time");
        }
        QueryWrapper<HealthRecord> wrapper = new QueryWrapper<HealthRecord>()
                .eq("elder_id", elderId).orderByDesc("record_time");
        if (startTime != null) {
            wrapper.ge("record_time", startTime);
        }
        if (endTime != null) {
            wrapper.le("record_time", endTime);
        }
        Page<HealthRecord> page = recordMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        return PageResponse.from(page, page.getRecords().stream().map(assembler::toRecord)
                .collect(Collectors.toList()));
    }

    public DoctorResponses.HealthRecordResponse latest(Long elderId) {
        List<HealthRecord> records = recordMapper.selectList(new QueryWrapper<HealthRecord>()
                .eq("elder_id", elderId).orderByDesc("record_time").last("LIMIT 1"));
        return records.isEmpty() ? null : assembler.toRecord(records.get(0));
    }

    private void requireIndicator(HealthRecordRequest request) {
        if (request.getSystolicPressure() == null && request.getDiastolicPressure() == null
                && request.getBloodGlucose() == null && request.getHeartRate() == null
                && request.getBodyTemperature() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "At least one health indicator is required");
        }
        if ((request.getSystolicPressure() == null) != (request.getDiastolicPressure() == null)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Blood pressure requires both values");
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
