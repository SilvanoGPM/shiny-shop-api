package com.skyg0d.shop.shiny.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyg0d.shop.shiny.exception.BadRequestException;
import com.skyg0d.shop.shiny.model.EOrderStatus;
import com.skyg0d.shop.shiny.property.StripeProps;
import com.skyg0d.shop.shiny.service.OrderService;
import com.skyg0d.shop.shiny.service.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
public class StripeController {

    private final StripeProps stripeProps;
    private final OrderService orderService;
    private final StripeService stripeService;
    private final ObjectMapper mapper;

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody String json, HttpServletRequest request) throws StripeException {
        Event event;

        try {
            String sigHeader = request.getHeader("Stripe-Signature");

            event = Webhook.constructEvent(
                    json, sigHeader, stripeProps.getWebhookSecret()
            );
        } catch (SignatureVerificationException e) {
            throw new BadRequestException(e.getMessage());
        }

        if (event.getType().equals("checkout.session.completed")) {
            Map<String, Object> eventData = getMap(event.getData());
            Map<String, Object> objectProp = getMap(eventData.get("object"));
            Map<String, String> metadataProp = getMap(objectProp.get("metadata"));

            String email = metadataProp.get("email");
            String orderId = metadataProp.get("orderId");

            if (email == null || email.isBlank()) {
                throw new BadRequestException("Empty email on checkout!");
            }

            stripeService.desactivePaymentLink(orderService.findById(orderId).getPaymentLink().getPaymentId());
            orderService.adminChangeStatus(orderId, EOrderStatus.PAID, "Order canceled. Could not pay.");
            orderService.removePaymentLink(orderId);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private <T> Map<String, T> getMap(Object value) {
        @SuppressWarnings("unchecked")
        Map<String, T> map = mapper.convertValue(value, Map.class);

        return map;
    }
}
