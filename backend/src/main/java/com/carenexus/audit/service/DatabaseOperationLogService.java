package com.carenexus.audit.service;

import com.carenexus.audit.OperationLogService;
import com.carenexus.audit.entity.OperationLog;
import com.carenexus.audit.mapper.OperationLogMapper;
import com.carenexus.auth.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DatabaseOperationLogService implements OperationLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseOperationLogService.class);

    private final OperationLogMapper operationLogMapper;

    public DatabaseOperationLogService(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    @Override
    public void record(CurrentUser operator, String action, String targetType, Long targetId, String result) {
        OperationLog log = new OperationLog();
        if (operator != null) {
            log.setOperatorId(operator.getUserId());
            log.setOperatorName(operator.getUsername());
        }
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setResult(result);
        log.setDetailSummary("security audit event");
        try {
            operationLogMapper.insert(log);
        } catch (RuntimeException ex) {
            LOGGER.warn("Failed to record operation log action={}, result={}", action, result);
        }
    }
}
