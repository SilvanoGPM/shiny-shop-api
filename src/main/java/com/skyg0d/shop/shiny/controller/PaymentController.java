package com.skyg0d.shop.shiny.controller;

import com.skyg0d.shop.shiny.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final StripeService stripeService;

    @PostMapping("/charge")
    public ResponseEntity<Charge> chargeCard(HttpServletRequest request) throws StripeException {
        String token = request.getHeader("token");
        double amount = Double.parseDouble(request.getHeader("amount"));

        return ResponseEntity.ok(stripeService.chargeCard(token, amount));
    }

}
