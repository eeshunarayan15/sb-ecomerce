package com.ecommerce.sbecom.security;
import com.ecommerce.sbecom.model.Provider;
import com.ecommerce.sbecom.model.RefreshToken;
import com.ecommerce.sbecom.model.User;
import com.ecommerce.sbecom.repository.RefreshTokenRepository;
import com.ecommerce.sbecom.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuthSucessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final RefreshTokenRepository refreshTokenRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //http://localhost:8089/oauth2/authorization/google
        //Google Callback	http://localhost:8089/login/oauth2/code/google
        //React Success	http://localhost:5173/oauth2/redirect?token=...
        log.info("OAuth Login Successful! Processing user data...");
        // 1. Google se user details nikalna
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String authorizedClientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
        log.info("Authorized Client Registration ID: {}", authorizedClientRegistrationId);

        // OAuthSucessHandler mein ye check lagayein
        Boolean isEmailVerified = oauth2User.getAttribute("email_verified");
        if (isEmailVerified != null && !isEmailVerified) {
            throw new OAuth2AuthenticationException("Email not verified by Google");
        }
        assert oauth2User != null;
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture");
        User user = userRepository.findByEmail(email)
                .map(existingUser -> {
                    // Agar user pehle se hai, toh uski details update kar dein (Optional but good)
                    existingUser.setFirstName(name);
                    existingUser.setImageUrl(picture);
                    existingUser.setProvider(Provider.GOOGLE); // Provider sync ensure karein
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    // Naya user banane ka logic (Aapka existing code)
                    log.info("New OAuth User detected. Registering: {}", email);
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFirstName(name);
                    newUser.setImageUrl(picture);
                    newUser.setProvider(Provider.GOOGLE);
                    newUser.setEnabled(true);
                    return userRepository.save(newUser);
                });


        // 3. Apne JWT Tokens generate karein
        String accessToken = jwtService.generateToken(user);
        String jti = UUID.randomUUID().toString();
        String refreshToken = jwtService.generateRefreshToken(user, jti);
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .jti(jti)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshTokenEntity); // Persistence must hai!
        log.info("Refresh Token persisted in DB with JTI: {}", jti);

        // 4. Refresh Token Cookie mein set karein
        // cookieService.attachRefreshCookie(response, refreshToken, 7 * 24 * 60 * 60);

        // // 5. React Vite (5173) par redirect karein Access Token ke saath
        // String targetUrl = "http://localhost:5173/oauth2/redirect?token=" + accessToken;

        // log.info("Redirecting user to React: {}", targetUrl);
        // response.sendRedirect(targetUrl);
        // 4. Set both access and refresh token cookies
        cookieService.attachAccessCookie(response, accessToken, cookieService.getAccessTtlSeconds());
        cookieService.attachRefreshCookie(response, refreshToken, cookieService.getRefreshTtlSeconds());

        // 5. Redirect to React home page (no token in URL)
        String targetUrl = "http://localhost:5173";

        log.info("Redirecting user to React: {}", targetUrl);
        response.sendRedirect(targetUrl);
    }
}
