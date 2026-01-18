package com.ecommerce.sbecom.securityconfig;
import com.ecommerce.sbecom.security.JwtsAuthenticationFilter;
import com.ecommerce.sbecom.security.OAuthFailureHandler;
import com.ecommerce.sbecom.security.OAuthSucessHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Slf4j
public class SecurityConfig {
    private final JwtsAuthenticationFilter jwtsAuthenticationFilter;
    private final OAuthSucessHandler oAuthSucessHandler;
    private final OAuthFailureHandler oAuthFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(

                        authorizeHttpRequests -> authorizeHttpRequests
                                .requestMatchers("/api/v1/auth/signin").permitAll()
                                .requestMatchers("/api/v1/auth/signup").permitAll()
                                .requestMatchers("/api/v1/auth/refreshtoken").permitAll()
                                .requestMatchers("/", "/error").permitAll()
                                .requestMatchers("/api/v1/auth/logout").permitAll() // ADD THIS LINE
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                                .requestMatchers("/v3/api-doc/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                                .requestMatchers("/", "/error", "/favicon.ico").permitAll().anyRequest().authenticated()

                )
                .oauth2Login(oauth2Login -> oauth2Login.successHandler(oAuthSucessHandler)
                        .failureHandler(oAuthFailureHandler))
                .logout(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                    // response.sendError(401, "Unauthorized");
                    // // Log the exception for debugging purposes
                    // authException.printStackTrace();
                    // response.setContentType("application/json");
                    // String message = "Unauthorised access" + authException.getMessage();
                    // Map<String, String> errorMap = Map.of("message", message, "status",
                    // String.valueOf(401), "statusCode", Integer.toString(401));
                    // var objectMapper = new ObjectMapper();
                    // response.getWriter().write(objectMapper.writeValueAsString(errorMap));
                    // 1. Trace hatane ke liye printStackTrace() ko delete karein
                    log.error("Unauthorized access at {}: {}", request.getRequestURI(), authException.getMessage());

                    // 2. Response setup
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");

                    Map<String, String> errorMap = Map.of("message", "Unauthorized: " + authException.getMessage(),
                            "status", "401");

                    // Spring ka default ObjectMapper use karein (Performance ke liye)
                    new ObjectMapper().writeValue(response.getWriter(), errorMap);

                })).addFilterBefore(jwtsAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.front-end.url}") String frontendUrl) {
        String[] urls = frontendUrl.trim().split(",");
        var config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(urls));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
