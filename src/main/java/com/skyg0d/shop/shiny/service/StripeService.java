package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.property.StripeProps;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.param.ChargeCreateParams;
import com.stripe.param.CustomerCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    private final StripeProps stripeProps;
    private final UserService userService;

    @Autowired
    public StripeService(StripeProps stripeProps, UserService userService) {
        this.stripeProps = stripeProps;
        this.userService = userService;

        Stripe.apiKey = stripeProps.getSecretKey();
    }

    public Charge chargeCard(String token, double amount) throws StripeException {
        ChargeCreateParams chargeParams = ChargeCreateParams
                .builder()
                .setAmount((long) (amount * 100))
                .setCurrency(stripeProps.getCurrency())
                .setSource(token)
                .build();

        return Charge.create(chargeParams);
    }

    public Customer createCustomer(String token, String email) throws StripeException {
        CustomerCreateParams customerParams = CustomerCreateParams
                .builder()
                .setEmail(email)
                .setSource(token)
                .build();

        Customer customer = Customer.create(customerParams);

        userService.addCustomerId(email, customer.getId());

        return customer;
    }

    public Charge chargeCustomerCard(String customerId, long amount) throws StripeException {
        String sourceCard = Customer.retrieve(customerId).getDefaultSource();

        ChargeCreateParams chargeParams = ChargeCreateParams
                .builder()
                .setAmount(amount)
                .setCurrency(stripeProps.getCurrency())
                .setCustomer(customerId)
                .setSource(sourceCard)
                .build();

        return Charge.create(chargeParams);
    }

}
