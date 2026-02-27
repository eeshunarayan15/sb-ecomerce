package com.ecommerce.sbecom.stripe.service;

import com.ecommerce.sbecom.model.User;
import com.ecommerce.sbecom.repository.UserRepository;
import com.ecommerce.sbecom.stripe.dto.StripePaymentDto;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl {
    @Value("${stripe.secret.key}")
    private String stripeApiKey;
    private final UserRepository userRepository;
    public PaymentIntent paymentIntent(StripePaymentDto stripePaymentDto, User user) throws StripeException {


        Stripe.apiKey =stripeApiKey;
        String customerId = user.getStripeCustomerId();
        if (customerId == null) {
            // First time → Create new Stripe customer
            CustomerCreateParams customerParams = CustomerCreateParams.builder()
                    .setEmail(user.getEmail())
                    .setName(user.getFullName())
                    .build();

            Customer customer = Customer.create(customerParams);
            customerId = customer.getId();

            // Save to DB for reuse next time!
            user.setStripeCustomerId(customerId);
            userRepository.save(user);
        }
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(stripePaymentDto.getAmount())
                        .setCurrency("usd")
                        .setCustomer(customerId)
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        return PaymentIntent.create(params);
    }
}
