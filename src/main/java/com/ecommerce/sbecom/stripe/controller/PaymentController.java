package com.ecommerce.sbecom.stripe.controller;

import com.ecommerce.sbecom.model.User;
import com.ecommerce.sbecom.stripe.dto.StripePaymentDto;
import com.ecommerce.sbecom.stripe.service.StripeServiceImpl;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {
    private final StripeServiceImpl stripeServiceimpl;
    @PostMapping("/order/stipe-client-secret")
    public ResponseEntity<String> createStripeClientSecret(
            @RequestBody StripePaymentDto stripePaymentDto,
             Authentication authentication
    ) throws StripeException {
        User user = (User) authentication.getPrincipal();
        assert user != null;
        PaymentIntent paymentIntent = stripeServiceimpl.paymentIntent(stripePaymentDto,user);
        log.info("stripe client secret: {}",paymentIntent.getClientSecret());
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentIntent.getClientSecret());

    }
}
