package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.exception.*;
import com.skyg0d.shop.shiny.mapper.OrderMapper;
import com.skyg0d.shop.shiny.model.*;
import com.skyg0d.shop.shiny.payload.ProductCalculate;
import com.skyg0d.shop.shiny.payload.request.CreateOrderProduct;
import com.skyg0d.shop.shiny.payload.request.CreateOrderRequest;
import com.skyg0d.shop.shiny.payload.response.OrderResponse;
import com.skyg0d.shop.shiny.payload.search.OrderParameterSearch;
import com.skyg0d.shop.shiny.repository.OrderRepository;
import com.skyg0d.shop.shiny.repository.specification.OrderSpecification;
import com.skyg0d.shop.shiny.security.service.UserDetailsImpl;
import com.skyg0d.shop.shiny.util.AuthUtils;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentLink;
import com.stripe.param.PaymentLinkCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    private final StripeService stripeService;

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

    public Page<OrderResponse> search(OrderParameterSearch search, Pageable pageable) {
        return orderRepository.findAll(OrderSpecification.getSpecification(search), pageable).map(mapper::toOrderResponse);
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request, String email) throws StripeException {
        User user = userService.findByEmail(email);

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
                .status(EOrderStatus.WAITING)
                .price(price)
                .products(productsIds)
                .user(user)
                .build();

        Order orderSaved = orderRepository.save(order);
        PaymentLink paymentLink = createPaymentLink(request, email, orderSaved.getId().toString());

        orderSaved.setPaymentLink(
                MyPaymentLink
                        .builder()
                        .paymentId(paymentLink.getId())
                        .paymentUrl(paymentLink.getUrl())
                        .build()
        );

        return mapper.toOrderResponse(orderRepository.save(orderSaved));
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

    public void removePaymentLink(String id) {
        Order orderFound = findById(id);

        orderFound.setPaymentLink(null);

        orderRepository.save(orderFound);
    }

    public void adminChangeStatus(String id, EOrderStatus status, String errMessage) {
        Order orderFound = findById(id);

        if (orderFound.getStatus().equals(EOrderStatus.CANCELED)) {
            throw new OrderStatusException(errMessage);
        }

        updateStatus(orderFound, status);
    }

    private PaymentLink createPaymentLink(CreateOrderRequest request, String email, String orderId) throws StripeException {
        List<ProductCalculate> products = getProducts(request.getProducts());

        List<PaymentLinkCreateParams.LineItem> productsStripePrices = products
                .stream()
                .map((rawProduct) -> {
                    String stripePriceId = productService.findBySlug(rawProduct.getSlug()).getStripePriceId();

                    return PaymentLinkCreateParams.LineItem
                            .builder()
                            .setPrice(stripePriceId)
                            .setQuantity(rawProduct.getAmount())
                            .build();
                })
                .collect(Collectors.toList());

        return stripeService.createPaymentLink(productsStripePrices, email, orderId);
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
