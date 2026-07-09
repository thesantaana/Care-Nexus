package com.carenexus.auth;

import com.carenexus.auth.token.TokenClaims;
import java.time.Duration;

public interface TokenService {

    String createToken(CurrentUser currentUser, Duration expiresIn);

    TokenClaims parseToken(String token);

    TokenClaims parseToken(String token, boolean allowRevoked);

    boolean revokeToken(String token);
}
