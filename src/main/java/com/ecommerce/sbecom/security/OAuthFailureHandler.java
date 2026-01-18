package com.ecommerce.sbecom.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        
        log.error("OAuth2 Login Failed: {}", exception.getMessage());

        // User ko wapas React ke login page par bhejien ek error parameter ke saath
        String targetUrl = "http://localhost:5173/login?error=" + exception.getMessage();
        
        // Redirect karna
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}