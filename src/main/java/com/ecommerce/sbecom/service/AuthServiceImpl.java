package com.ecommerce.sbecom.service;
import com.ecommerce.sbecom.dto.LoginRequest;
import com.ecommerce.sbecom.dto.LoginResponse;
import com.ecommerce.sbecom.dto.RefreshTokenRequest;
import com.ecommerce.sbecom.dto.UserDto;
import com.ecommerce.sbecom.exception.UserAlreadyExistsException;
import com.ecommerce.sbecom.model.Provider;
import com.ecommerce.sbecom.model.RefreshToken;

import com.ecommerce.sbecom.model.User;
import com.ecommerce.sbecom.repository.RefreshTokenRepository;
import com.ecommerce.sbecom.repository.UserRepository;
import com.ecommerce.sbecom.security.CookieService;
import com.ecommerce.sbecom.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieService cookieService;
@Transactional
    @Override
    public LoginResponse register(LoginRequest loginRequest) {
        // logic
        // verify email
        log.info("Registering user with email: {}", loginRequest.getEmail());
        if (userRepository.existsByEmail(loginRequest.getEmail())) {
            throw new UserAlreadyExistsException("User Already exist with email " + loginRequest.getEmail());
        }
        loginRequest.setPassword(passwordEncoder.encode(loginRequest.getPassword()));
//        User user = modelMapper.map(loginRequest, User.class);


        User user = User.builder()
                .email(loginRequest.getEmail())
                .password(loginRequest.getPassword())
                .provider(Provider.LOCAL)
                .enabled(true)
                .build();
        // Set<Role> role = new HashSet<>();
        // role.add(new Role("USER"));
        // user.setRoles(role);
        // System.out.println(user.toString()+"user");
        // System.out.println(user.getRoles().toString()+"user");

        User savUser = userRepository.save(user);
        String token = jwtService.generateToken(savUser);
        String email = savUser.getEmail();

        return LoginResponse.builder()
//                .userDto(modelMapper.map(savUser, UserDto.class))
                .userDto(UserDto.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .enabled(user.isEnabled())
                        .provider(user.getProvider())


                        .build())
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {

        try {
            long start = System.currentTimeMillis();
            // 1. Spring Security khud user fetch karegi aur password verify karegi
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            log.info("Authentication took: {}ms", System.currentTimeMillis() - start);
            // 2. REDUNDANT CALL HATAYEIN: Dobara findByEmail karne ki zarurat nahi hai
            // Authentication se hi user object nikaalein
            User user = (User) authenticate.getPrincipal();

            assert user != null;
            if (!user.isEnabled()) {
                throw new BadCredentialsException("User is not enabled");
            }
            // JTI ka matlab hota hai JWT ID. Ye har token ka ek unique aadhar card number
            // jaisa hota hai.
            // JTI ka asli kaam kya hai?
            // Unique Identity: Ek hi user ke paas 2-3 tokens ho sakte hain (laptop aur
            // phone ke liye). JTI se server ko pata chalta hai ki kaun sa token kis device
            // ka hai.
            //
            // Chori se bachao (Security): Agar koi hacker aapka purana token use karne ki
            // koshish kare, toh server check karta hai ki "kya ye jti pehle use ho chuka
            // hai?" Agar haan, toh wo usse block kar deta hai.
            //
            // Logout Feature: Jab aap "Logout from this device" par click karte ho, toh
            // server sirf us specific jti wale token ko database se delete ya revoke
            // (cancel) karta hai.
            String jti = UUID.randomUUID().toString();
            LocalDateTime now = LocalDateTime.now();
            var refreshTokenEntity = RefreshToken.builder()
                    .jti(jti)
                    .user(user)
                    .createdAt(now)
                    .expiredAt(now.plusMinutes(30))
                    .revoked(false)
                    .build();
            start = System.currentTimeMillis();
            RefreshToken savedToken = refreshTokenRepository.save(refreshTokenEntity);
            log.info("Save refresh token took: {}ms", System.currentTimeMillis() - start);
            String accessToken = jwtService.generateToken(user);
            String refreshTokenString = jwtService.generateRefreshToken(user, refreshTokenEntity.getJti());
            // use cookie service to attach refresh token in cookie
            cookieService.attachRefreshCookie(response, refreshTokenString, cookieService.getRefreshTtlSeconds());
            cookieService.attachAccessCookie(response, accessToken, cookieService.getAccessTtlSeconds());
            cookieService.addNoStoreHeaders(response);
            return LoginResponse
                    .builder()

                    .expiresIn(refreshTokenEntity.getExpiredAt())
                    .userDto(UserDto.builder()
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .email(user.getEmail())
                            .build())
                    .build();

        } catch (Exception e) {
            throw new BadCredentialsException("Bad credentials");
        }

    }

    @Override
    @Transactional
    public LoginResponse resfreshtoken(RefreshTokenRequest refreshTokenRequest, HttpServletRequest httpServletRequest,
                                       HttpServletResponse response) {
        log.info("A - start refresh");
        String refreshToken = readRefreshTokenFromRequest(httpServletRequest, refreshTokenRequest)
                .orElseThrow(() -> new BadCredentialsException("Invalid Refresh Token"));
        log.info("B - token read");
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Invalid Refresh Token type");
        }
        String jti = jwtService.getJti(refreshToken);
        log.info("C - jti parsed");
        UUID userIdFromToken = jwtService.getUserIdFromToken(refreshToken);
        log.info("D - userId parsed");

        RefreshToken refreshTokenFromDb = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new BadCredentialsException("Jti Not present in db"));
        log.info("E - token loaded from DB");
        if (refreshTokenFromDb.isRevoked()) {
            throw new BadCredentialsException("Refresh token is revoked");
        }
        if (refreshTokenFromDb.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Refresh token is expired");
        }
        if (!refreshTokenFromDb.getUser().getId().equals(userIdFromToken)) {
            throw new BadCredentialsException("Refresh Token Does not belong to this user");
        }
        log.info("F - token validated");
        // refresh token rotation
        refreshTokenFromDb.setRevoked(true);
        String newJti = UUID.randomUUID().toString();
        refreshTokenFromDb.setReplacedByToken(newJti);
        log.info("G - old token revoked");
        User user = refreshTokenFromDb.getUser();
        log.info("H - user loaded");
        var newRefreshTokenEntity = RefreshToken.builder()
                .jti(newJti)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(30))
                .revoked(false)
                .build();
        refreshTokenRepository.save(newRefreshTokenEntity);
        log.info("I - new token saved");

        String accessToken = jwtService.generateToken(user);
        log.info("J - access token generated");
        String newRefreshToken = jwtService.generateRefreshToken(user, newJti);
        log.info("K - refresh token generated");
        cookieService.attachAccessCookie(response, accessToken, cookieService.getAccessTtlSeconds()); // ADD THIS LINE
        cookieService.attachRefreshCookie(response, newRefreshToken, cookieService.getRefreshTtlSeconds());

        cookieService.addNoStoreHeaders(response);
        log.info("L - cookies attached");
        LoginResponse loginResponse = LoginResponse.builder()
                
                .expiresIn(newRefreshTokenEntity.getExpiredAt())
                .userDto(UserDto.builder()
                        .id(user.getId().toString())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .phone(user.getPhone())
                        .createdAt(user.getCreatedAt())
                        .enabled(user.isEnabled())
                        .provider(user.getProvider())
                
                .build())
                .build();
        log.info("Refresh token response: {}", loginResponse);
        log.info("M - refresh finished");
        return loginResponse;
    }

    private Optional<String> readRefreshTokenFromRequest(HttpServletRequest httpServletRequest,
            RefreshTokenRequest refreshTokenRequest) {

        if (httpServletRequest.getCookies() != null) {
            Optional<String> fromCookie = Arrays.stream(
                    httpServletRequest.getCookies())
                    .filter(c -> cookieService.getRefreshTokenCookieName().equals(c.getName()))
                    .map(Cookie::getValue)
                    .filter(v -> !v.isBlank())
                    .findFirst();

            if (fromCookie.isPresent()) {
                return fromCookie;
            }
        }
        if (refreshTokenRequest != null && refreshTokenRequest.getRefreshToken() != null
                && !refreshTokenRequest.getRefreshToken().isBlank()) {
            return Optional.of(refreshTokenRequest.getRefreshToken());
        }
        // custom header
        String fromHeader = httpServletRequest.getHeader("X-Refresh-Token");
        if (fromHeader != null && !fromHeader.isBlank()) {
            return Optional.of(fromHeader);
        }

        return Optional.empty();
    }

}
