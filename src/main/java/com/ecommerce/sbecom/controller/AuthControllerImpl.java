package com.ecommerce.sbecom.controller;


import com.ecommerce.sbecom.dto.*;
import com.ecommerce.sbecom.model.User;
import com.ecommerce.sbecom.repository.RefreshTokenRepository;
import com.ecommerce.sbecom.repository.UserRepository;
import com.ecommerce.sbecom.security.CookieService;
import com.ecommerce.sbecom.security.JwtService;
import com.ecommerce.sbecom.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/v1/auth")
@Slf4j

@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {
    private final AuthService authService;
    private  final CookieService cookieService;
    private  final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private  final UserRepository userRepository;
//    private  final ModelMapper modelMapper;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Object>> register(@Valid @RequestBody LoginRequest loginRequest) {

        LoginResponse register = authService.register(loginRequest);


        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(true)
                .data(register)
                .message("User registered successfully")
                .timestamp(LocalDateTime.now().toString())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/signin")
    @Override
    public ResponseEntity<ApiResponse<Object>> signin(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse res = authService.login(loginRequest, response);
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(true)
                .data(res)
                .message("User registered successfully")
                .timestamp(LocalDateTime.now().toString())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);


    }

    @PostMapping("/refreshtoken")
    @Override
    public ResponseEntity<ApiResponse<Object>> resfreshtoken(
            @RequestBody(required = false) RefreshTokenRequest refreshTokenRequest,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        LoginResponse resfreshtoken = authService.resfreshtoken(refreshTokenRequest, httpServletRequest, httpServletResponse);

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(true)
                .data(resfreshtoken)
                .message("User registered successfully")
                .timestamp(LocalDateTime.now().toString())
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {

        // 1. Request ki Cookies mein se Refresh Token nikalna
        String refreshToken = cookieService.getCookieValue(request, cookieService.getRefreshTokenCookieName());

        if (refreshToken != null) {
            try {
                // 2. Check karna ki ye naya wala "Refresh" token hi hai
                if (jwtService.isRefreshToken(refreshToken)) {

                    // 3. Token ke andar se uski unique ID (JTI) nikalna
                    String jti = jwtService.getJti(refreshToken);

                    // 4. DATABASE ACTION: Us specific JTI wale token ko 'revoked' mark karna
                    refreshTokenRepository.findByJti(jti).ifPresent(tokenEntity -> {
                        tokenEntity.setRevoked(true);
                        refreshTokenRepository.save(tokenEntity);
                        log.info("Successfully revoked token in DB for JTI: {}", jti);
                    });
                }
            } catch (Exception e) {
                // Agar token corrupt hai ya expire ho gaya hai, toh sirf log karein
                log.warn("Logout: Could not process token revocation in DB: {}", e.getMessage());
            }
        }

        // 5. BROWSER ACTION: Browser ko command dena ki 'refresh-token' cookie delete kar de
        cookieService.clearRefreshCookie(response);

        // 6. SECURITY: Browser cache clear karne ke headers (taaki back button dabane par data na dikhe)
        cookieService.addNoStoreHeaders(response);

        return ResponseEntity.noContent().build(); // 204 No Content return karein
    }
    // In AuthController
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Object>> getCurrentUser(
            @AuthenticationPrincipal User user
    ) {
        log.info("getCurrentUser called, user is: {}", user != null ? user.getEmail() : "NULL");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Unauthorized")
                            .data(null)
                            .timestamp(LocalDateTime.now().toString())
                            .build());
        }

        UserDto userDto = UserDto.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .enabled(user.isEnabled())
                .provider(user.getProvider())
                .build();
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(true)
                .data(userDto)
                .message("User registered successfully")
                .timestamp(LocalDateTime.now().toString())
                .build();
        return ResponseEntity
                .ok(apiResponse);
    }

}
