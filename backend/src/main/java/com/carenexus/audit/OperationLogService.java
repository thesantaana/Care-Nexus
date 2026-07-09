package com.carenexus.audit;

import com.carenexus.auth.CurrentUser;

public interface OperationLogService {

    void record(CurrentUser operator, String action, String targetType, Long targetId, String result);
}
