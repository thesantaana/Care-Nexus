package com.carenexus.care;

import com.carenexus.auth.CurrentUser;

public interface FamilyElderAccessService {

    boolean canAccessElder(CurrentUser currentUser, Long elderId);

    boolean canBindElder(CurrentUser currentUser, Long elderId);
}
