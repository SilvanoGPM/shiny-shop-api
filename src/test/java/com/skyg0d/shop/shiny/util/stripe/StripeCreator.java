package com.skyg0d.shop.shiny.util.stripe;

import com.skyg0d.shop.shiny.util.order.OrderCreator;
import com.skyg0d.shop.shiny.util.product.ProductCreator;
import com.stripe.model.PaymentLink;
import com.stripe.model.Price;
import com.stripe.model.Product;
import org.springframework.security.core.parameters.P;

import java.math.BigDecimal;

public class StripeCreator {

    public static Product createStripeProduct() {
        Product stripeProduct =  new Product();

        stripeProduct.setId(ProductCreator.STRIPE_PRODUCT_ID);
        stripeProduct.setName(ProductCreator.NAME);
        stripeProduct.setDescription(ProductCreator.DESCRIPTION);

        return stripeProduct;
    }

    public static Price createStripePrice() {
        Price stripePrice = new Price();

        stripePrice.setId(ProductCreator.STRIPE_PRICE_ID);
        stripePrice.setUnitAmountDecimal(new BigDecimal(100));

        return stripePrice;
    }

    public static PaymentLink createPaymentLink() {
        PaymentLink paymentLink = new PaymentLink();

        paymentLink.setId(OrderCreator.STRIPE_PAYMENT_ID);
        paymentLink.setUrl(OrderCreator.STRIPE_PAYMENT_URL);

        return paymentLink;
    }

}
