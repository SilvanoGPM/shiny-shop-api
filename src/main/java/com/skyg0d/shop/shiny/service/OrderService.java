package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.exception.*;
import com.skyg0d.shop.shiny.mapper.OrderMapper;
import com.skyg0d.shop.shiny.model.EOrderStatus;
import com.skyg0d.shop.shiny.model.Order;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.ProductCalculate;
import com.skyg0d.shop.shiny.payload.request.CreateOrderProduct;
import com.skyg0d.shop.shiny.payload.request.CreateOrderRequest;
import com.skyg0d.shop.shiny.payload.response.OrderResponse;
import com.skyg0d.shop.shiny.repository.OrderRepository;
import com.skyg0d.shop.shiny.security.service.UserDetailsImpl;
import com.skyg0d.shop.shiny.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final ProductService productService;

    private final UserService userService;

    private final AuthUtils authUtils;

    private final OrderMapper mapper = OrderMapper.INSTANCE;

    public Page<OrderResponse> listAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(mapper::toOrderResponse);
    }

    public Page<OrderResponse> listAllByUser(Pageable pageable, String email) {
        return orderRepository.findAllByUser(pageable, userService.findByEmail(email)).map(mapper::toOrderResponse);
    }

    public Order findById(String id) throws ResourceNotFoundException {
        return orderRepository
                .findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    public OrderResponse findByIdMapped(String id) throws ResourceNotFoundException {
        return mapper.toOrderResponse(findById(id));
    }

    public OrderResponse create(CreateOrderRequest request) {
        User user = userService.findByEmail(request.getUserEmail());

        List<ProductCalculate> products = getProducts(request.getProducts());

        List<Product> productsIds = products
                .stream()
                .map((product) -> {
                    List<Product> totalProducts = new ArrayList<>();

                    for (int i = 0; i < product.getAmount(); i++) {
                        Product productCreated = Product
                                .builder()
                                .id(product.getId())
                                .slug(product.getSlug())
                                .build();

                        totalProducts.add(productCreated);
                    }

                    return totalProducts;
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());

        BigDecimal price = products
                .stream()
                .map(ProductCalculate::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order
                .builder()
                .status(EOrderStatus.SHIPPING)
                .price(price)
                .products(productsIds)
                .user(user)
                .build();

        return mapper.toOrderResponse(orderRepository.save(order));
    }

    public void cancelOrder(String id) throws OrderStatusException {
        Order orderFound = findById(id);

        if (orderFound.getStatus().equals(EOrderStatus.DELIVERED)) {
            throw new OrderStatusException("Order already delivered, could not cancel.");
        }

        UserDetailsImpl userDetails = authUtils.getUserDetails();

        boolean isOwner = userDetails.getEmail().equals(orderFound.getUser().getEmail());

        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch((authority) ->
                authority.getAuthority().equals("ROLE_ADMIN")
        );

        if (!isOwner && !isAdmin) {
            throw new OrderPermissionInsufficient();
        }

        updateStatus(orderFound, EOrderStatus.CANCELED);
    }

    public void adminChangeStatus(String id, EOrderStatus status, String errMessage) {
        Order orderFound = findById(id);

        if (orderFound.getStatus().equals(EOrderStatus.CANCELED)) {
            throw new OrderStatusException(errMessage);
        }

        updateStatus(orderFound, status);
    }

    private void updateStatus(Order order, EOrderStatus status) {
        order.setStatus(status);

        orderRepository.save(order);
    }

    private List<ProductCalculate> getProducts(List<CreateOrderProduct> products) throws InactiveProductOnOrderException, ProductOverflowAmountException {
        return products
                .stream()
                .map((productRaw) -> {
                    Product product = productService.findBySlug(productRaw.getSlug());

                    checkProductValid(productRaw, product);

                    long amount = product.getAmount() - productRaw.getAmount();
                    productService.changeAmount(product.getSlug(), amount);

                    BigDecimal price = product.getPrice();

                    if (product.getDiscount() > 0) {
                        BigDecimal discount = new BigDecimal(product.getDiscount());
                        BigDecimal percentage = discount.divide(new BigDecimal(100), 2, RoundingMode.DOWN);

                        price = price.subtract(price.multiply(percentage));
                    }

                    price = price.multiply(new BigDecimal(productRaw.getAmount()));

                    return ProductCalculate
                            .builder()
                            .slug(product.getSlug())
                            .id(product.getId())
                            .price(price)
                            .amount(productRaw.getAmount())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private void checkProductValid(CreateOrderProduct productRaw, Product product) throws InactiveProductOnOrderException, ProductOverflowAmountException {
        if (!product.isActive()) {
            throw new InactiveProductOnOrderException(productRaw.getSlug());
        }

        boolean haveEnoughProducts = productRaw.getAmount() <= product.getAmount();

        if (!haveEnoughProducts) {
            throw new ProductOverflowAmountException(product.getSlug(), product.getAmount());
        }
    }

}
