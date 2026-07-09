package com.carenexus.doctor;

import com.carenexus.auth.CurrentUser;

public interface DoctorAuthorizationService {

    boolean canAccessElder(CurrentUser currentUser, Long elderId);
}
