package com.skyg0d.shop.shiny.controller;

import com.skyg0d.shop.shiny.model.EOrderStatus;
import com.skyg0d.shop.shiny.model.Order;
import com.skyg0d.shop.shiny.payload.request.CreateOrderRequest;
import com.skyg0d.shop.shiny.payload.response.MessageResponse;
import com.skyg0d.shop.shiny.payload.response.OrderResponse;
import com.skyg0d.shop.shiny.payload.search.OrderParameterSearch;
import com.skyg0d.shop.shiny.security.service.UserDetailsImpl;
import com.skyg0d.shop.shiny.service.OrderService;
import com.skyg0d.shop.shiny.util.AuthUtils;
import com.skyg0d.shop.shiny.util.MockUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static com.skyg0d.shop.shiny.util.auth.AuthCreator.*;
import static com.skyg0d.shop.shiny.util.order.OrderCreator.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for OrderController")
public class OrderControllerTest {

    @InjectMocks
    OrderController orderController;

    @Mock
    OrderService orderService;

    @Mock
    AuthUtils authUtils;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        PageImpl<OrderResponse> ordersPage = new PageImpl<>(List.of(createOrderResponse()));

        BDDMockito
                .when(orderService.listAll(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(ordersPage);

        BDDMockito
                .when(orderService.listAllByUser(ArgumentMatchers.any(Pageable.class), ArgumentMatchers.anyString()))
                .thenReturn(ordersPage);

        BDDMockito
                .when(orderService.findByIdMapped(ArgumentMatchers.anyString()))
                .thenReturn(createOrderResponse());

        BDDMockito
                .when(orderService.search(ArgumentMatchers.any(OrderParameterSearch.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(ordersPage);

        BDDMockito
                .when(orderService.create(ArgumentMatchers.any(CreateOrderRequest.class), ArgumentMatchers.anyString()))
                .thenReturn(createOrderResponse());

        BDDMockito
                .when(authUtils.getUserDetails())
                .thenReturn(new UserDetailsImpl(UUID.randomUUID(), USERNAME, EMAIL, PASSWORD, null));

        BDDMockito
                .doNothing()
                .when(orderService)
                .cancelOrder(ArgumentMatchers.anyString());

        BDDMockito
                .doNothing()
                .when(orderService)
                .adminChangeStatus(ArgumentMatchers.anyString(), ArgumentMatchers.any(EOrderStatus.class), ArgumentMatchers.anyString());

    }

    @Test
    @DisplayName("listAll Returns List Of Orders Inside Page Object When Successful")
    void listAll_ReturnsListOfCategoriesInsidePageObject_WhenSuccessful() {
        OrderResponse expectedOrder = createOrderResponse();

        ResponseEntity<Page<OrderResponse>> entity = orderController.listAll(PageRequest.of(0, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotEmpty();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getId()).isEqualTo(expectedOrder.getId());
    }

    @Test
    @DisplayName("listAllByUser Returns List Of Orders Inside Page Object When Successful")
    void listAllByUser_ReturnsListOfCategoriesInsidePageObject_WhenSuccessful() {
        MockUtils.mockSecurityContextHolder();

        OrderResponse expectedOrder = createOrderResponse();

        ResponseEntity<Page<OrderResponse>> entity = orderController.listAllByUser(PageRequest.of(0, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotEmpty();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getId()).isEqualTo(expectedOrder.getId());
    }

    @Test
    @DisplayName("findById Returns Order When Successful")
    void findById_ReturnsOrder_WhenSuccessful() {
        Order expectedOrder = createOrder();

        ResponseEntity<OrderResponse> entity = orderController.findById(UUID.randomUUID().toString());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getId()).isEqualTo(expectedOrder.getId());
    }

    @Test
    @DisplayName("search Returns List Of Orders Inside Page Object When Successful")
    void search_ReturnsListOfCategoriesInsidePageObject_WhenSuccessful() {
        OrderResponse expectedOrder = createOrderResponse();

        ResponseEntity<Page<OrderResponse>> entity = orderController.search(createOrderParameterSearch(), PageRequest.of(0, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotEmpty();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getId()).isEqualTo(expectedOrder.getId());
    }

    @Test
    @DisplayName("mySearch Returns List Of Orders Inside Page Object When Successful")
    void mySearch_ReturnsListOfCategoriesInsidePageObject_WhenSuccessful() {
        OrderResponse expectedOrder = createOrderResponse();

        ResponseEntity<Page<OrderResponse>> entity = orderController.mySearch(createOrderParameterSearch(), PageRequest.of(0, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotEmpty();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getId()).isEqualTo(expectedOrder.getId());
    }

    @Test
    @DisplayName("create Persists Order When Successful")
    @SneakyThrows
    void create_PersistsOrder_WhenSuccessful() {
        OrderResponse expectedOrder = createOrderResponse();

        ResponseEntity<OrderResponse> entity = orderController.create(createCreateOrderRequest());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getId()).isEqualTo(expectedOrder.getId());
    }

    @Test
    @DisplayName("cancelOrder Updates Order Status When Successful")
    void cancelOrder_UpdatesOrderStatus_WhenSuccessful() {
        String expectedMessage = "Order canceled";

        ResponseEntity<MessageResponse> entity = orderController.cancelOrder(UUID.randomUUID().toString());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("shipOrder Updates Order Status When Successful")
    void shipOrder_UpdatesOrderStatus_WhenSuccessful() {
        String expectedMessage = "Order shipped";

        ResponseEntity<MessageResponse> entity = orderController.shipOrder(UUID.randomUUID().toString());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("onTheWayOrder Updates Order Status When Successful")
    void onTheWayOrder_UpdatesOrderStatus_WhenSuccessful() {
        String expectedMessage = "Order on the way";

        ResponseEntity<MessageResponse> entity = orderController.onTheWayOrder(UUID.randomUUID().toString());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("deliverOrder Updates Order Status When Successful")
    void deliverOrder_UpdatesOrderStatus_WhenSuccessful() {
        String expectedMessage = "Order delivered";

        ResponseEntity<MessageResponse> entity = orderController.deliverOrder(UUID.randomUUID().toString());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);
    }

}
