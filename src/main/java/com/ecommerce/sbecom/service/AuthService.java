package com.ecommerce.sbecom.service;
import com.ecommerce.sbecom.dto.LoginRequest;
import com.ecommerce.sbecom.dto.LoginResponse;
import com.ecommerce.sbecom.dto.RefreshTokenRequest;
import com.ecommerce.sbecom.dto.SignUpRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface AuthService {
    //register user
   LoginResponse register(
           @Valid SignUpRequest loginRequest);
   //login register
    LoginResponse login(
            LoginRequest loginRequest ,
            HttpServletResponse response);


    LoginResponse resfreshtoken(
            RefreshTokenRequest refreshTokenRequest,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse);
}
