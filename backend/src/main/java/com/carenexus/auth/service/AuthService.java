package com.carenexus.auth.service;

import com.carenexus.auth.dto.LoginRequest;
import com.carenexus.auth.vo.CurrentUserResponse;
import com.carenexus.auth.vo.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    CurrentUserResponse currentUser();

    void logout(String authorizationHeader);
}
