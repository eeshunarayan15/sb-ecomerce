package com.ecommerce.sbecom.service;
import com.ecommerce.sbecom.dto.LoginRequest;
import com.ecommerce.sbecom.dto.LoginResponse;
import com.ecommerce.sbecom.dto.RefreshTokenRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
public interface AuthService {
    //register user
   LoginResponse register(
           LoginRequest loginRequest);
   //login register
    LoginResponse login(
            LoginRequest loginRequest ,
            HttpServletResponse response);


    LoginResponse resfreshtoken(
            RefreshTokenRequest refreshTokenRequest,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse);
}
