package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.payload.PromotionCodeCreated;
import com.skyg0d.shop.shiny.payload.request.CreateProductRequest;
import com.skyg0d.shop.shiny.property.StripeProps;
import com.skyg0d.shop.shiny.util.product.ProductCreator;
import com.stripe.model.*;
import com.stripe.param.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashSet;

import static com.skyg0d.shop.shiny.util.product.ProductCreator.*;
import static com.skyg0d.shop.shiny.util.stripe.StripeCreator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for StripeService")
public class StripeServiceTest {

    @InjectMocks
    StripeService stripeService = new StripeService(new StripeProps("pk_test", "sk_test", "BRL", "", ""));

    @Test
    @DisplayName("retrieveProduct Returns Stripe Product When Successful")
    @SneakyThrows
    void retrieveProduct_ReturnsStripeProduct_WhenSuccessful() {
        try (MockedStatic<Product> staticProduct = BDDMockito.mockStatic(Product.class)) {
            staticProduct
                    .when(() -> Product.retrieve(ArgumentMatchers.anyString()))
                    .thenReturn(createStripeProduct());

            Product expectedProduct = createStripeProduct();

            Product productFound = stripeService.retrieveProduct(expectedProduct.getId());

            assertThat(productFound).isNotNull();

            assertThat(productFound.getId()).isEqualTo(expectedProduct.getId());
        }
    }

    @Test
    @DisplayName("createProduct Persists Stripe Product When Successful")
    @SneakyThrows
    void createProduct_PersistsStripeProduct_WhenSuccessful() {
        try (MockedStatic<Product> staticProduct = BDDMockito.mockStatic(Product.class)) {
            staticProduct
                    .when(() -> Product.create(ArgumentMatchers.any(ProductCreateParams.class)))
                    .thenReturn(createStripeProduct());

            CreateProductRequest expectedProduct = createCreateProductRequest();

            Product productFound = stripeService.createProduct(expectedProduct);

            assertThat(productFound).isNotNull();

            assertThat(productFound.getId()).isEqualTo(STRIPE_PRODUCT_ID);

            assertThat(productFound.getName()).isEqualTo(expectedProduct.getName());

            assertThat(productFound.getDescription()).isEqualTo(expectedProduct.getDescription());
        }
    }

    @Test
    @DisplayName("createPrice Persists Stripe Price When Successful")
    @SneakyThrows
    void createPrice_PersistsStripePrice_WhenSuccessful() {
        try (MockedStatic<Price> staticPrice = BDDMockito.mockStatic(Price.class)) {
            staticPrice
                    .when(() -> Price.create(ArgumentMatchers.any(PriceCreateParams.class)))
                    .thenReturn(createStripePrice());

            Price expectedPrice = createStripePrice();

            Price priceFound = stripeService.createPrice(STRIPE_PRODUCT_ID, expectedPrice.getUnitAmountDecimal());

            assertThat(priceFound).isNotNull();

            assertThat(priceFound.getId()).isEqualTo(STRIPE_PRICE_ID);

            assertThat(priceFound.getUnitAmountDecimal()).isEqualTo(expectedPrice.getUnitAmountDecimal());
        }
    }

    @Test
    @DisplayName("createPaymentLink Persists Stripe PaymentLink When Successful")
    @SneakyThrows
    void createPaymentLink_PersistsStripePaymentLink_WhenSuccessful() {
        try (MockedStatic<PaymentLink> staticPrice = BDDMockito.mockStatic(PaymentLink.class)) {
            staticPrice
                    .when(() -> PaymentLink.create(ArgumentMatchers.any(PaymentLinkCreateParams.class)))
                    .thenReturn(createPaymentLink());

            PaymentLink expectedPaymentLink = createPaymentLink();

            PaymentLink paymentLinkFound = stripeService.createPaymentLink(new ArrayList<>(), "some-email", "some-order-id");

            assertThat(paymentLinkFound).isNotNull();

            assertThat(paymentLinkFound.getId()).isEqualTo(expectedPaymentLink.getId());

            assertThat(paymentLinkFound.getUrl()).isEqualTo(expectedPaymentLink.getUrl());
        }
    }

    @Test
    @DisplayName("createPromotionCode Persists Stripe Promotion Code When Successful")
    @SneakyThrows
    void createPromotionCode_PersistsStripePromotionCode_WhenSuccessful() {
        try (MockedStatic<Coupon> staticCoupon = BDDMockito.mockStatic(Coupon.class)) {
            try (MockedStatic<PromotionCode> staticPromotionCode = BDDMockito.mockStatic(PromotionCode.class)) {

                staticCoupon
                        .when(() -> Coupon.create(ArgumentMatchers.any(CouponCreateParams.class)))
                        .thenReturn(createCoupon());

                staticPromotionCode
                        .when(() -> PromotionCode.create(ArgumentMatchers.any(PromotionCodeCreateParams.class)))
                        .thenReturn(createPromotionCode());

                Coupon expectedCoupon = createCoupon();

                PromotionCode expectedPromotionCode = createPromotionCode();

                PromotionCodeCreated promotionCodeCreated = stripeService.createPromotionCode(ProductCreator.createApplyDiscountParams(), STRIPE_PRODUCT_ID);

                assertThat(promotionCodeCreated).isNotNull();

                assertThat(promotionCodeCreated.getCouponId()).isEqualTo(expectedCoupon.getId());

                assertThat(promotionCodeCreated.getPromotionCodeId()).isEqualTo(expectedPromotionCode.getId());
            }
        }
    }

    @Test
    @DisplayName("updateProduct Updates Stripe Product When Successful")
    @SneakyThrows
    void updateProduct_UpdatesStripeProduct_WhenSuccessful() {
        try (MockedStatic<Product> staticProduct = BDDMockito.mockStatic(Product.class)) {
            Product productMock = BDDMockito.mock(Product.class);

            BDDMockito
                    .when(productMock.update(ArgumentMatchers.any(ProductUpdateParams.class)))
                    .thenReturn(createStripeProduct());

            staticProduct
                    .when(() -> Product.retrieve(ArgumentMatchers.anyString()))
                    .thenReturn(productMock);

            assertThatCode(() -> stripeService.updateProduct(STRIPE_PRODUCT_ID, ProductUpdateParams.builder().build()))
                    .doesNotThrowAnyException();
        }
    }

    @Test
    @DisplayName("updateProductMetadata Updates Metadata Of Stripe Product When Successful")
    @SneakyThrows
    void updateProductMetadata_UpdatesMetadataOfStripeProduct_WhenSuccessful() {
        try (MockedStatic<Product> staticProduct = BDDMockito.mockStatic(Product.class)) {
            Product productMock = BDDMockito.mock(Product.class);

            BDDMockito
                    .when(productMock.update(ArgumentMatchers.any(ProductUpdateParams.class)))
                    .thenReturn(createStripeProduct());

            staticProduct
                    .when(() -> Product.retrieve(ArgumentMatchers.anyString()))
                    .thenReturn(productMock);

            assertThatCode(() -> stripeService.updateProductMetadata(STRIPE_PRODUCT_ID, new HashSet<>()))
                    .doesNotThrowAnyException();
        }
    }

    @Test
    @DisplayName("setProductActive Updates Visibility Of Stripe Product When Successful")
    @SneakyThrows
    void setProductActive_UpdatesVisibilityOfStripeProduct_WhenSuccessful() {
        try (MockedStatic<Product> staticProduct = BDDMockito.mockStatic(Product.class)) {
            Product productMock = BDDMockito.mock(Product.class);

            BDDMockito
                    .when(productMock.update(ArgumentMatchers.any(ProductUpdateParams.class)))
                    .thenReturn(createStripeProduct());

            staticProduct
                    .when(() -> Product.retrieve(ArgumentMatchers.anyString()))
                    .thenReturn(productMock);

            assertThatCode(() -> stripeService.setProductActive(STRIPE_PRODUCT_ID, true))
                    .doesNotThrowAnyException();
        }
    }

    @Test
    @DisplayName("desactivePrice Updates Visibility Of Stripe Price To False When Successful")
    @SneakyThrows
    void desactivePrice_UpdatesVisibilityOfStripePriceToFalse_WhenSuccessful() {
        try (MockedStatic<Price> staticPrice = BDDMockito.mockStatic(Price.class)) {
            Price priceMock = BDDMockito.mock(Price.class);

            BDDMockito
                    .when(priceMock.update(ArgumentMatchers.any(PriceUpdateParams.class)))
                    .thenReturn(createStripePrice());

            staticPrice
                    .when(() -> Price.retrieve(ArgumentMatchers.anyString()))
                    .thenReturn(priceMock);

            assertThatCode(() -> stripeService.desactivePrice(STRIPE_PRICE_ID))
                    .doesNotThrowAnyException();
        }
    }

    @Test
    @DisplayName("desactivePaymentLink Updates Visibility Of Stripe PaymentLink To False When Successful")
    @SneakyThrows
    void desactivePaymentLink_UpdatesVisibilityOfStripePaymentLinkToFalse_WhenSuccessful() {
        try (MockedStatic<PaymentLink> staticPrice = BDDMockito.mockStatic(PaymentLink.class)) {
            PaymentLink paymentLinkMock = BDDMockito.mock(PaymentLink.class);

            BDDMockito
                    .when(paymentLinkMock.update(ArgumentMatchers.any(PaymentLinkUpdateParams.class)))
                    .thenReturn(createPaymentLink());

            staticPrice
                    .when(() -> PaymentLink.retrieve(ArgumentMatchers.anyString()))
                    .thenReturn(paymentLinkMock);

            assertThatCode(() -> stripeService.desactivePaymentLink("some-link"))
                    .doesNotThrowAnyException();
        }
    }

    @Test
    @DisplayName("deleteProduct Removes Stripe Product When Successful")
    @SneakyThrows
    void deleteProduct_RemovesStripeProduct_WhenSuccessful() {
        try (MockedStatic<Product> staticProduct = BDDMockito.mockStatic(Product.class)) {
            Product productMock = BDDMockito.mock(Product.class);

            BDDMockito
                    .when(productMock.delete())
                    .thenReturn(createStripeProduct());

            staticProduct
                    .when(() -> Product.retrieve(ArgumentMatchers.anyString()))
                    .thenReturn(productMock);

            assertThatCode(() -> stripeService.deleteProduct(STRIPE_PRODUCT_ID))
                    .doesNotThrowAnyException();
        }
    }

    @Test
    @DisplayName("deletePromotionCode Removes Stripe PromotionCode When Successful")
    @SneakyThrows
    void deletePromotionCode_RemovesStripePromotionCode_WhenSuccessful() {
        try (MockedStatic<Coupon> staticCoupon = BDDMockito.mockStatic(Coupon.class)) {
            try (MockedStatic<PromotionCode> staticPromotionCode = BDDMockito.mockStatic(PromotionCode.class)) {
                Coupon couponMock = BDDMockito.mock(Coupon.class);

                BDDMockito
                        .when(couponMock.delete())
                        .thenReturn(createCoupon());

                staticCoupon
                        .when(() -> Coupon.retrieve(ArgumentMatchers.anyString()))
                        .thenReturn(couponMock);

                PromotionCode promotionCodeMock = BDDMockito.mock(PromotionCode.class);

                BDDMockito
                        .when(promotionCodeMock.update(ArgumentMatchers.any(PromotionCodeUpdateParams.class)))
                        .thenReturn(createPromotionCode());

                staticPromotionCode
                        .when(() -> PromotionCode.retrieve(ArgumentMatchers.anyString()))
                        .thenReturn(promotionCodeMock);

                assertThatCode(() -> stripeService.deletePromotionCode(STRIPE_PROMOTION_CODE_ID, STRIPE_COUPON_ID))
                        .doesNotThrowAnyException();
            }
        }
    }

}
