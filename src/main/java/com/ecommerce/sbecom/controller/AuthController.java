package com.ecommerce.sbecom.controller;

import com.ecommerce.sbecom.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthController {
    public ResponseEntity<ApiResponse<Object>> register(SignUpRequest signUpRequest) ;
   ResponseEntity<LoginResponse> signin(LoginRequest loginRequest , HttpServletResponse response);
   //access and refresh token renew karne ke liye api
    ResponseEntity<ApiResponse<Object>> resfreshtoken(RefreshTokenRequest refreshTokenRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
}
