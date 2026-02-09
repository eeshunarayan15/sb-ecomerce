package com.ecommerce.sbecom.security;


import com.ecommerce.sbecom.model.User;
import com.ecommerce.sbecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@Cacheable
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private static AtomicInteger callCount = new AtomicInteger(0);

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userDetails", key = "#username")
    public UserDetails loadUserByUsername(String username) {
        int count = callCount.incrementAndGet();
        log.info("=== loadUserByUsername called {} times for: {} ===", count, username);

        // Show MORE of the stack trace
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        log.info(">>> Caller[0]: {}.{} (line {})",
                stack[2].getClassName(), stack[2].getMethodName(), stack[2].getLineNumber());
        log.info(">>> Caller[1]: {}.{} (line {})",
                stack[3].getClassName(), stack[3].getMethodName(), stack[3].getLineNumber());
        log.info(">>> Caller[2]: {}.{} (line {})",
                stack[4].getClassName(), stack[4].getMethodName(), stack[4].getLineNumber());
        log.info(">>> Caller[3]: {}.{} (line {})",
                stack[5].getClassName(), stack[5].getMethodName(), stack[5].getLineNumber());

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        log.info("User found: {}, roles count: {}", user.getEmail(), user.getRoles().size());

        // Return User directly - it implements UserDetails!
        return user;
    }


}
