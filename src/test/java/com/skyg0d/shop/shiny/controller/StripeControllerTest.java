package com.skyg0d.shop.shiny.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyg0d.shop.shiny.exception.BadRequestException;
import com.skyg0d.shop.shiny.property.StripeProps;
import com.skyg0d.shop.shiny.service.OrderService;
import com.skyg0d.shop.shiny.service.StripeService;
import com.skyg0d.shop.shiny.util.order.OrderCreator;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for StripeController")
public class StripeControllerTest {

    @InjectMocks
    StripeController stripeController;

    @Mock
    StripeProps stripeProps;

    @Mock
    OrderService orderService;

    @Mock
    StripeService stripeService;

    @Mock
    ObjectMapper objectMapper;

    @BeforeEach
    @SuppressWarnings({"unchecked", "rawtypes"})
    void setUp() {
        BDDMockito
                .when(stripeProps.getWebhookSecret())
                .thenReturn("whkey");

        Map mockedMap = BDDMockito.mock(Map.class);

        BDDMockito
                .when(mockedMap.get(ArgumentMatchers.any()))
                .thenReturn(mockedMap, "some-string");

        BDDMockito
                .when(orderService.findById(ArgumentMatchers.anyString()))
                .thenReturn(OrderCreator.createOrder());

        BDDMockito
                .when(objectMapper.convertValue(ArgumentMatchers.any(), ArgumentMatchers.any(Class.class)))
                .thenReturn(mockedMap);
    }

    @Test
    @DisplayName("webhook Listen Stripe Requests When Successful")
    @SneakyThrows
    void webhook_ListenStripeRequests_WhenSuccessful() {
        try (MockedStatic<Webhook> staticWebhook = BDDMockito.mockStatic(Webhook.class)) {
            Event mockedEvent = BDDMockito.mock(Event.class);

            HttpServletRequest mockedRequest = BDDMockito.mock(HttpServletRequest.class);

            BDDMockito
                    .when(mockedRequest.getHeader(ArgumentMatchers.anyString()))
                    .thenReturn("some-header");

            BDDMockito
                    .when(mockedEvent.getType())
                    .thenReturn("checkout.session.completed");

            staticWebhook
                    .when(() -> Webhook.constructEvent(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                    .thenReturn(mockedEvent);

            ResponseEntity<Void> entity = stripeController.webhook("some-json", mockedRequest);

            assertThat(entity).isNotNull();

            assertThat(entity.getBody()).isNull();

            assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Test
    @DisplayName("webhook Throws BadRequestException When Has Invalid Stripe Field")
    @SneakyThrows
    void webhook_ThrowsBadRequestException_WhenHasInvalidStripeField() {
        try (MockedStatic<Webhook> staticWebhook = BDDMockito.mockStatic(Webhook.class)) {
            HttpServletRequest mockedRequest = BDDMockito.mock(HttpServletRequest.class);

            BDDMockito
                    .when(mockedRequest.getHeader(ArgumentMatchers.anyString()))
                    .thenReturn("some-header");

            staticWebhook
                    .when(() -> Webhook.constructEvent(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                    .thenThrow(SignatureVerificationException.class);

            assertThatExceptionOfType(BadRequestException.class)
                    .isThrownBy(() -> stripeController.webhook("some-json", mockedRequest));
        }
    }

    @Test
    @DisplayName("webhook Throws BadRequestException When Email Is Empty")
    @SneakyThrows
    @SuppressWarnings({"unchecked", "rawtypes"})
    void webhook_ThrowsBadRequestException_WhenEmailIsEmpty() {
        Map mockedMap = BDDMockito.mock(Map.class);

        BDDMockito
                .when(mockedMap.get(ArgumentMatchers.any()))
                .thenReturn(mockedMap, "");

        BDDMockito
                .when(objectMapper.convertValue(ArgumentMatchers.any(), ArgumentMatchers.any(Class.class)))
                .thenReturn(mockedMap);

        try (MockedStatic<Webhook> staticWebhook = BDDMockito.mockStatic(Webhook.class)) {
            Event mockedEvent = BDDMockito.mock(Event.class);

            HttpServletRequest mockedRequest = BDDMockito.mock(HttpServletRequest.class);

            BDDMockito
                    .when(mockedRequest.getHeader(ArgumentMatchers.anyString()))
                    .thenReturn("some-header");

            BDDMockito
                    .when(mockedEvent.getType())
                    .thenReturn("checkout.session.completed");

            staticWebhook
                    .when(() -> Webhook.constructEvent(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                    .thenReturn(mockedEvent);

            assertThatExceptionOfType(BadRequestException.class)
                    .isThrownBy(() -> stripeController.webhook("some-json", mockedRequest));
        }
    }

}
