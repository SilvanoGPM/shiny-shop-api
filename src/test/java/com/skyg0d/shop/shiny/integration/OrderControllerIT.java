package com.skyg0d.shop.shiny.integration;

import com.skyg0d.shop.shiny.exception.details.ExceptionDetails;
import com.skyg0d.shop.shiny.model.Category;
import com.skyg0d.shop.shiny.model.EOrderStatus;
import com.skyg0d.shop.shiny.model.Order;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.payload.request.CreateOrderProduct;
import com.skyg0d.shop.shiny.payload.request.CreateOrderRequest;
import com.skyg0d.shop.shiny.payload.response.MessageResponse;
import com.skyg0d.shop.shiny.payload.response.OrderResponse;
import com.skyg0d.shop.shiny.repository.CategoryRepository;
import com.skyg0d.shop.shiny.repository.OrderRepository;
import com.skyg0d.shop.shiny.repository.ProductRepository;
import com.skyg0d.shop.shiny.repository.UserRepository;
import com.skyg0d.shop.shiny.service.StripeService;
import com.skyg0d.shop.shiny.util.JWTCreator;
import com.skyg0d.shop.shiny.wrapper.PageableResponse;
import com.stripe.model.PaymentLink;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static com.skyg0d.shop.shiny.util.order.OrderCreator.createCreateOrderRequest;
import static com.skyg0d.shop.shiny.util.order.OrderCreator.createOrder;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Integration tests for OrderController")
public class OrderControllerIT {

    @MockBean
    StripeService stripeService;

    @Autowired
    TestRestTemplate httpClient;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JWTCreator jwtCreator;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        PaymentLink paymentLink = new PaymentLink();
        paymentLink.setId("test-payment-id");
        paymentLink.setUrl("test-payment-url");

        BDDMockito
                .when(stripeService.createPaymentLink(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(paymentLink);
    }

    @Test
    @DisplayName("listAll Returns List Of Orders Inside Page Object When Successful")
    void listAll_ReturnsListOfCategoriesInsidePageObject_WhenSuccessful() {
        Order expectedOrder = persistOrder();

        ResponseEntity<PageableResponse<OrderResponse>> entity = httpClient.exchange(
                "/orders",
                HttpMethod.GET,
                jwtCreator.createAdminAuthEntity(null),
                new ParameterizedTypeReference<>() {
                });

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotEmpty();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getId()).isEqualTo(expectedOrder.getId());

        assertThat(entity.getBody().getContent().get(0).getStatus()).isEqualTo(expectedOrder.getStatus());
    }

    @Test
    @DisplayName("listAllByUser Returns List Of Orders Inside Page Object When Successful")
    void listAllByUser_ReturnsListOfCategoriesInsidePageObject_WhenSuccessful() {
        Order expectedOrder = persistOrder();

        ResponseEntity<PageableResponse<OrderResponse>> entity = httpClient.exchange(
                "/orders/my",
                HttpMethod.GET,
                jwtCreator.createUserAuthEntity(null),
                new ParameterizedTypeReference<>() {
                });

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
        Order expectedOrder = persistOrder();

        ResponseEntity<OrderResponse> entity = httpClient.exchange(
                "/orders/{uuid}",
                HttpMethod.GET,
                null,
                OrderResponse.class,
                expectedOrder.getId()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getId()).isEqualTo(expectedOrder.getId());
    }

    @Test
    @DisplayName("search Returns List Of Orders Inside Page Object When Successful")
    void search_ReturnsListOfCategoriesInsidePageObject_WhenSuccessful() {
        Order expectedOrder = persistOrder();

        ResponseEntity<PageableResponse<OrderResponse>> entity = httpClient.exchange(
                "/orders/search",
                HttpMethod.GET,
                jwtCreator.createAdminAuthEntity(null),
                new ParameterizedTypeReference<>() {
                });

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
        Order expectedOrder = persistOrder();

        ResponseEntity<PageableResponse<OrderResponse>> entity = httpClient.exchange(
                "/orders/my/search",
                HttpMethod.GET,
                jwtCreator.createUserAuthEntity(null),
                new ParameterizedTypeReference<>() {
                });

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotEmpty();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getId()).isEqualTo(expectedOrder.getId());
    }

    @Test
    @DisplayName("findById Returns ExceptionDetails When Order Don't Exists")
    void findById_ReturnsExceptionDetails_WhenOrderDoNotExists() {
        String expectedTitle = "Resource Not Found";

        ResponseEntity<ExceptionDetails> entity = httpClient.exchange(
                "/orders/{id}",
                HttpMethod.GET,
                null,
                ExceptionDetails.class,
                UUID.randomUUID().toString()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getTitle()).isEqualTo(expectedTitle);
    }

    @Test
    @DisplayName("create Persists Order When Successful")
    void create_PersistsOrder_WhenSuccessful() {
        persistOrder();

        CreateOrderRequest request = createCreateOrderRequest();

        request.setProducts(List.of(new CreateOrderProduct(createOrder().getProducts().get(0).getSlug(), 1, "")));

        ResponseEntity<OrderResponse> entity = httpClient.exchange(
                "/orders",
                HttpMethod.POST,
                jwtCreator.createUserAuthEntity(request),
                OrderResponse.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getUser()).isNotNull();

        assertThat(entity.getBody().getUser().getEmail()).isEqualTo(jwtCreator.createUser().getEmail());
    }

    @Test
    @DisplayName("create Returns ExceptionDetails When Order Has Invalid Product")
    void create_ReturnsExceptionDetails_WhenOrderHasInvalidProduct() {
        persistOrder(EOrderStatus.SHIPPING, 10, false);

        CreateOrderRequest request = createCreateOrderRequest();

        request.setProducts(List.of(new CreateOrderProduct(createOrder().getProducts().get(0).getSlug(), 1, "")));

        String expectedTitle = "Inactive Product On Order";

        ResponseEntity<ExceptionDetails> entity = httpClient.exchange(
                "/orders",
                HttpMethod.POST,
                jwtCreator.createUserAuthEntity(request),
                ExceptionDetails.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getTitle()).isEqualTo(expectedTitle);
    }

    @Test
    @DisplayName("create Returns ExceptionDetails When Order Product No Stock")
    void create_ReturnsExceptionDetails_WhenOrderProductNoStock() {
        persistOrder(EOrderStatus.SHIPPING, 0);

        CreateOrderRequest request = createCreateOrderRequest();

        request.setProducts(List.of(new CreateOrderProduct(createOrder().getProducts().get(0).getSlug(), 1, "")));

        String expectedTitle = "Product Amount Lacking";

        ResponseEntity<ExceptionDetails> entity = httpClient.exchange(
                "/orders",
                HttpMethod.POST,
                jwtCreator.createUserAuthEntity(request),
                ExceptionDetails.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getTitle()).isEqualTo(expectedTitle);
    }

    @Test
    @DisplayName("cancelOrder Updates Order Status When Successful")
    void cancelOrder_UpdatesOrderStatus_WhenSuccessful() {
        ResponseEntity<Void> entity = httpClient.exchange(
                "/orders/{id}/cancel",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(null),
                Void.class,
                persistOrder().getId()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("cancelOrder Returns ExceptionDetails When Order Already Delivered")
    void cancelOrder_ReturnsExceptionDetails_WhenOrderAlreadyDelivered() {
        String expectedTitle = "Order Status Incorrect";

        ResponseEntity<ExceptionDetails> entity = httpClient.exchange(
                "/orders/{id}/cancel",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(null),
                ExceptionDetails.class,
                persistOrder(EOrderStatus.DELIVERED).getId()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getTitle()).isEqualTo(expectedTitle);
    }

    @Test
    @DisplayName("cancelOrder Returns ExceptionDetails When Order User Has Insufficient Permission")
    void cancelOrder_ReturnsExceptionDetails_WhenOrderUserHasInsufficientPermission() {
        String expectedTitle = "Order Permission Insufficient";

        ResponseEntity<ExceptionDetails> entity = httpClient.exchange(
                "/orders/{id}/cancel",
                HttpMethod.PATCH,
                jwtCreator.createModeratorAuthEntity(null),
                ExceptionDetails.class,
                persistOrder().getId()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getTitle()).isEqualTo(expectedTitle);
    }

    @Test
    @DisplayName("shipOrder Updates Order Status When Successful")
    void shipOrder_UpdatesOrderStatus_WhenSuccessful() {
        ResponseEntity<Void> entity = httpClient.exchange(
                "/orders/{id}/ship",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(null),
                Void.class,
                persistOrder().getId()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("shipOrder Returns ExceptionDetails When Already Canceled")
    void shipOrder_ReturnsExceptionDetails_WhenOrderAlreadyCanceled() {
        String expectedTitle = "Order Status Incorrect";

        ResponseEntity<ExceptionDetails> entity = httpClient.exchange(
                "/orders/{id}/ship",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(null),
                ExceptionDetails.class,
                persistOrder(EOrderStatus.CANCELED).getId()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getTitle()).isEqualTo(expectedTitle);
    }

    @Test
    @DisplayName("onTheWayOrder Updates Order Status When Successful")
    void onTheWayOrder_UpdatesOrderStatus_WhenSuccessful() {
        ResponseEntity<Void> entity = httpClient.exchange(
                "/orders/{id}/otw",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(null),
                Void.class,
                persistOrder().getId()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("onTheWayOrder Returns ExceptionDetails When Order Already Canceled")
    void onTheWayOrder_ReturnsExceptionDetails_WhenOrderAlreadyCanceled() {
        String expectedTitle = "Order Status Incorrect";

        ResponseEntity<ExceptionDetails> entity = httpClient.exchange(
                "/orders/{id}/otw",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(null),
                ExceptionDetails.class,
                persistOrder(EOrderStatus.CANCELED).getId()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getTitle()).isEqualTo(expectedTitle);
    }

    @Test
    @DisplayName("deliverOrder Updates Order Status When Successful")
    void deliverOrder_UpdatesOrderStatus_WhenSuccessful() {
        ResponseEntity<Void> entity = httpClient.exchange(
                "/orders/{id}/deliver",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(null),
                Void.class,
                persistOrder().getId()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("deliverOrder Returns ExceptionDetails When Order Already Canceled")
    void deliverOrder_ReturnsExceptionDetails_WhenOrderAlreadyCanceled() {
        String expectedTitle = "Order Status Incorrect";

        ResponseEntity<ExceptionDetails> entity = httpClient.exchange(
                "/orders/{id}/deliver",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(null),
                ExceptionDetails.class,
                persistOrder(EOrderStatus.CANCELED).getId()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getTitle()).isEqualTo(expectedTitle);
    }

    private Order persistOrder() {
        return persistOrder(EOrderStatus.WAITING);
    }

    private Order persistOrder(EOrderStatus status) {
        return persistOrder(status, 10);
    }

    private Order persistOrder(EOrderStatus status, long amount) {
        return persistOrder(status, amount, true);
    }

    private Order persistOrder(EOrderStatus status, long amount, boolean active) {
        Order orderToBeSave = createOrder();

        Product productToBeSave = orderToBeSave.getProducts().get(0);
        productToBeSave.setAmount(amount);
        productToBeSave.setActive(active);
        List<Category> categoriesSaved = categoryRepository.saveAllAndFlush(productToBeSave.getCategories());
        productToBeSave.setCategories(new HashSet<>(categoriesSaved));
        Product productSaved = productRepository.save(productToBeSave);

        orderToBeSave.setProducts(List.of(productSaved));
        orderToBeSave.setUser(userRepository.findByEmail(jwtCreator.createUser().getEmail()).orElseThrow());
        orderToBeSave.setStatus(status);

        return orderRepository.save(orderToBeSave);
    }

}
