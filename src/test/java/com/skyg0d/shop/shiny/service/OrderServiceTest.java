package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.exception.*;
import com.skyg0d.shop.shiny.model.EOrderStatus;
import com.skyg0d.shop.shiny.model.Order;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.response.OrderResponse;
import com.skyg0d.shop.shiny.repository.OrderRepository;
import com.skyg0d.shop.shiny.security.service.UserDetailsImpl;
import com.skyg0d.shop.shiny.util.AuthUtils;
import com.skyg0d.shop.shiny.util.user.UserCreator;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.skyg0d.shop.shiny.util.order.OrderCreator.*;
import static com.skyg0d.shop.shiny.util.product.ProductCreator.createProduct;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for OrderService")
public class OrderServiceTest {

    @InjectMocks
    OrderService orderService;

    @Mock
    OrderRepository orderRepository;

    @Mock
    ProductService productService;

    @Mock
    UserService userService;

    @Mock
    AuthUtils authUtils;

    @BeforeEach
    void setUp() {
        PageImpl<Order> ordersPage = new PageImpl<>(List.of(createOrder()));

        BDDMockito
                .when(orderRepository.findAll(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(ordersPage);

        BDDMockito
                .when(orderRepository.findAllByUser(ArgumentMatchers.any(Pageable.class), ArgumentMatchers.any(User.class)))
                .thenReturn(ordersPage);

        BDDMockito
                .when(orderRepository.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(createOrder()));

        BDDMockito
                .when(userService.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(createOrder().getUser());

        BDDMockito
                .when(productService.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(createProduct());

        BDDMockito
                .doNothing()
                .when(productService)
                .changeAmount(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong());

        BDDMockito
                .when(orderRepository.save(ArgumentMatchers.any(Order.class)))
                .thenReturn(createOrder());

        BDDMockito
                .when(authUtils.getUserDetails())
                .thenReturn(new UserDetailsImpl(UUID.randomUUID(), UserCreator.USERNAME, UserCreator.EMAIL, UserCreator.PASSWORD, List.of(new SimpleGrantedAuthority("ADMIN"))));
    }

    @Test
    @DisplayName("listAll Returns List Of Orders Inside Page Object When Successful")
    void listAll_ReturnsListOfCategoriesInsidePageObject_WhenSuccessful() {
        OrderResponse expectedOrder = createOrderResponse();

        Page<OrderResponse> ordersPage = orderService.listAll(PageRequest.of(0, 1));

        assertThat(ordersPage).isNotEmpty();

        assertThat(ordersPage.getContent()).isNotEmpty();

        assertThat(ordersPage.getContent().get(0)).isNotNull();

        assertThat(ordersPage.getContent().get(0).getId()).isEqualTo(expectedOrder.getId());
    }

    @Test
    @DisplayName("listAllByUser Returns List Of Orders Inside Page Object When Successful")
    void listAllByUser_ReturnsListOfCategoriesInsidePageObject_WhenSuccessful() {
        OrderResponse expectedOrder = createOrderResponse();

        Page<OrderResponse> ordersPage = orderService.listAllByUser(PageRequest.of(0, 1), "some-email");

        assertThat(ordersPage).isNotEmpty();

        assertThat(ordersPage.getContent()).isNotEmpty();

        assertThat(ordersPage.getContent().get(0)).isNotNull();

        assertThat(ordersPage.getContent().get(0).getId()).isEqualTo(expectedOrder.getId());
    }

    @Test
    @DisplayName("findById Returns Order When Successful")
    void findById_ReturnsOrder_WhenSuccessful() {
        Order expectedOrder = createOrder();

        Order orderFound = orderService.findById(UUID.randomUUID().toString());

        assertThat(orderFound).isNotNull();

        assertThat(orderFound.getId()).isEqualTo(expectedOrder.getId());
    }

    @Test
    @DisplayName("findById Throws ResourceNotFoundException When Order Don't Exists")
    void findById_ThrowsResourceNotFoundException_WhenOrderDoNotExists() {
        BDDMockito
                .when(orderRepository.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> orderService.findById(UUID.randomUUID().toString()));
    }

    @Test
    @DisplayName("findByIdMapped Returns Order When Successful")
    void findByIdMapped_ReturnsOrder_WhenSuccessful() {
        OrderResponse expectedOrder = createOrderResponse();

        OrderResponse orderFound = orderService.findByIdMapped(UUID.randomUUID().toString());

        assertThat(orderFound).isNotNull();

        assertThat(orderFound.getId()).isEqualTo(expectedOrder.getId());
    }

    @Test
    @DisplayName("findByIdMapped Throws ResourceNotFoundException When Order Don't Exists")
    void findByIdMapped_ThrowsResourceNotFoundException_WhenOrderDoNotExists() {
        BDDMockito
                .when(orderRepository.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> orderService.findByIdMapped(UUID.randomUUID().toString()));
    }

    @Test
    @DisplayName("create Persists Order When Successful")
    void create_PersistsOrder_WhenSuccessful() {
        OrderResponse expectedOrder = createOrderResponse();

        OrderResponse orderFound = orderService.create(createCreateOrderRequest(), "test@mail.com");

        assertThat(orderFound).isNotNull();

        assertThat(orderFound.getId()).isEqualTo(expectedOrder.getId());
    }

    @Test
    @DisplayName("create Persists Order When Product Has Discount")
    void create_PersistsOrder_WhenProductHasDiscount() {
        Product product = createProduct();
        product.setDiscount(10);

        Order order = createOrder();
        order.setProducts(List.of(product));

        BDDMockito
                .when(productService.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(product);

        BDDMockito
                .when(orderRepository.save(ArgumentMatchers.any(Order.class)))
                .thenReturn(order);

        OrderResponse expectedOrder = createOrderResponse();

        OrderResponse orderFound = orderService.create(createCreateOrderRequest(), "test@mail.com");

        assertThat(orderFound).isNotNull();

        assertThat(orderFound.getId()).isEqualTo(expectedOrder.getId());

        assertThat(orderFound.getProducts()).isNotEmpty();

        assertThat(orderFound.getProducts().get(0)).isNotNull();

        assertThat(orderFound.getProducts().get(0).getDiscount()).isEqualTo(10);
    }

    @Test
    @DisplayName("create Throws InactiveProductOnOrderException When Order Has Invalid Product")
    void create_ThrowsInactiveProductOnOrderException_WhenOrderHasInvalidProduct() {
        Product product = createProduct();
        product.setActive(false);

        BDDMockito
                .when(productService.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(product);

        assertThatExceptionOfType(InactiveProductOnOrderException.class)
                .isThrownBy(() -> orderService.create(createCreateOrderRequest(), "test@mail.com"));
    }

    @Test
    @DisplayName("create Throws ProductOverflowAmountException When Product No Stock")
    void create_ThrowsProductOverflowAmountException_WhenProductNoStock() {
        Product product = createProduct();
        product.setAmount(0);

        BDDMockito
                .when(productService.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(product);

        assertThatExceptionOfType(ProductOverflowAmountException.class)
                .isThrownBy(() -> orderService.create(createCreateOrderRequest(), "test@mail.com"));
    }

    @Test
    @DisplayName("cancelOrder Updates Order Status When Successful")
    void cancelOrder_UpdatesOrderStatus_WhenSuccessful() {
        assertThatCode(() -> orderService.cancelOrder(UUID.randomUUID().toString()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("cancelOrder Throws OrderStatusException When Order Already Delivered")
    void cancelOrder_ThrowsOrderStatusException_WhenOrderAlreadyDelivered() {
        Order order = createOrder();
        order.setStatus(EOrderStatus.DELIVERED);

        BDDMockito
                .when(orderRepository.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(order));

        assertThatExceptionOfType(OrderStatusException.class)
                .isThrownBy(() -> orderService.cancelOrder(UUID.randomUUID().toString()));
    }

    @Test
    @DisplayName("cancelOrder Throws OrderPermissionInsufficient When Permission Is Insufficient")
    void cancelOrder_ThrowsOrderPermissionInsufficient_WhenPermissionIsInsufficient() {
        BDDMockito
                .when(authUtils.getUserDetails())
                .thenReturn(new UserDetailsImpl(UUID.randomUUID(), UserCreator.USERNAME, "random-email", UserCreator.PASSWORD, new ArrayList<>()));

        assertThatExceptionOfType(OrderPermissionInsufficient.class)
                .isThrownBy(() -> orderService.cancelOrder(UUID.randomUUID().toString()));
    }

    @Test
    @DisplayName("adminChangeStatus Updates Order Status When Successful")
    void adminChangeStatus_UpdatesOrderStatus_WhenSuccessful() {
        assertThatCode(() -> orderService.adminChangeStatus(UUID.randomUUID().toString(), EOrderStatus.ON_THE_WAY, "message-error"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("adminChangeStatus Throws OrderStatusException When Order Already Canceled")
    void adminChangeStatus_ThrowsOrderStatusException_WhenOrderAlreadyCanceled() {
        Order order = createOrder();
        order.setStatus(EOrderStatus.CANCELED);

        BDDMockito
                .when(orderRepository.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(order));

        assertThatExceptionOfType(OrderStatusException.class)
                .isThrownBy(() -> orderService.adminChangeStatus(UUID.randomUUID().toString(), EOrderStatus.ON_THE_WAY, "message-error"));
    }

}
