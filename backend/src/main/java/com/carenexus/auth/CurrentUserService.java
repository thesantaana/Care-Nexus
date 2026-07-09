package com.carenexus.auth;

import java.util.Optional;

public interface CurrentUserService {

    Optional<CurrentUser> getCurrentUser();

    Long requireCurrentUserId();

    CurrentUser loadActiveUser(Long userId);
}
