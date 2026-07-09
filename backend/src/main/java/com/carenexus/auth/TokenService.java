package com.carenexus.auth;

import java.time.Duration;

public interface TokenService {

    String createToken(CurrentUser currentUser, Duration expiresIn);

    CurrentUser parseToken(String token);

    boolean revokeToken(String token);
}
