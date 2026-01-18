package com.ecommerce.sbecom.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@Getter
public class CookieService {
    private final String refreshTokenCookieName;
    private final boolean cookieHttpOnly;
    private final boolean cookieSecure;
    private final String cookieDomain;
    private final String sameSite;
    private final String accessTokenCookieName;
    private final int refreshTtlSeconds;  // ADD THIS
    private final int accessTtlSeconds;   // ADD THIS

    public CookieService(
            @Value("${security.jwt.access-token-cookie-name}") String accessTokenCookieName,
            @Value("${security.jwt.refresh-token-cookie-name}") String refreshTokenCookieName,
            @Value("${security.jwt.cookie-http-only}") boolean cookieHttpOnly,
            @Value("${security.jwt.cookie-secure}") boolean cookieSecure,
            @Value("${security.jwt.cookie-domain}") String cookieDomain,
            @Value("${security.jwt.cookie-same-site}") String sameSite ,
            @Value("${security.jwt.refresh-ttl-seconds}") int refreshTtlSeconds
            ,@Value("${security.jwt.access-ttl-seconds}") int accessTtlSeconds)
    {
        this.refreshTokenCookieName = refreshTokenCookieName;
        this.cookieHttpOnly = cookieHttpOnly;
        this.cookieSecure = cookieSecure;
        this.cookieDomain = cookieDomain;
        this.sameSite = sameSite;
        this.accessTokenCookieName = accessTokenCookieName;
        this.accessTtlSeconds = accessTtlSeconds;   // ADD THIS
        this.refreshTtlSeconds = refreshTtlSeconds; // ADD THIS
    }

    // create method to attach cookie to response
    public void attachRefreshCookie(HttpServletResponse response, String value, int maxAge) {
        // create cookie
        var responseCookieBuilder = ResponseCookie.from(refreshTokenCookieName, value)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path("/")
                .maxAge(maxAge)
                .domain(cookieDomain)
                .sameSite(sameSite);

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            responseCookieBuilder.domain(cookieDomain);
        }
        ResponseCookie responseCookie = responseCookieBuilder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

    }

    public void attachAccessCookie(HttpServletResponse response, String value, int maxAge) {
        var responseCookieBuilder = ResponseCookie.from(accessTokenCookieName, value)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path("/")
                .maxAge(maxAge)
                .sameSite(sameSite);

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            responseCookieBuilder.domain(cookieDomain);
        }
        ResponseCookie responseCookie = responseCookieBuilder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    public void clearRefreshCookie(HttpServletResponse response) {
        var responseCookieBuilder = ResponseCookie.from(refreshTokenCookieName, "")
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)

                .sameSite(sameSite);

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            responseCookieBuilder.domain(cookieDomain);
        }
        ResponseCookie responseCookie = responseCookieBuilder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    // MISSING HELPER: Request se cookie read karne ke liye
    public String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null)
            return null;
        return java.util.Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .map(jakarta.servlet.http.Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public void addNoStoreHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }


    public void clearAccessCookie(HttpServletResponse response) {
        var responseCookieBuilder = ResponseCookie.from(accessTokenCookieName, "")
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0) // Immediately expire
                .sameSite(sameSite);

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            responseCookieBuilder.domain(cookieDomain);
        }
        ResponseCookie responseCookie = responseCookieBuilder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }
}
