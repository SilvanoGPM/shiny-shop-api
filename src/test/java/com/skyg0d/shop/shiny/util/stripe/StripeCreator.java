package com.skyg0d.shop.shiny.util.stripe;

import com.skyg0d.shop.shiny.util.order.OrderCreator;
import com.skyg0d.shop.shiny.util.product.ProductCreator;
import com.stripe.model.*;

import java.math.BigDecimal;

public class StripeCreator {

    public static final String COUPON_NAME = "Coupon Test";

    public static Product createStripeProduct() {
        Product stripeProduct = new Product();

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

    public static Coupon createCoupon() {
        Coupon coupon = new Coupon();

        coupon.setId(ProductCreator.STRIPE_COUPON_ID);
        coupon.setName(COUPON_NAME);

        return coupon;
    }

    public static PromotionCode createPromotionCode() {
        PromotionCode promotionCode = new PromotionCode();

        promotionCode.setId(ProductCreator.STRIPE_PROMOTION_CODE_ID);
        promotionCode.setCode(ProductCreator.DISCOUNT_CODE);

        return promotionCode;
    }

}
