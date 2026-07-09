package com.carenexus.care;

import com.carenexus.auth.CurrentUser;

public interface CareOrderAccessService {

    boolean canViewOrder(CurrentUser currentUser, Long orderId);

    boolean canOperateAssignedOrder(CurrentUser currentUser, Long orderId);
}
