package com.skyg0d.shop.shiny.controller;

import com.skyg0d.shop.shiny.annotations.IsAdmin;
import com.skyg0d.shop.shiny.model.EOrderStatus;
import com.skyg0d.shop.shiny.payload.request.CreateOrderRequest;
import com.skyg0d.shop.shiny.payload.response.MessageResponse;
import com.skyg0d.shop.shiny.payload.response.OrderResponse;
import com.skyg0d.shop.shiny.payload.search.OrderParameterSearch;
import com.skyg0d.shop.shiny.security.service.UserDetailsImpl;
import com.skyg0d.shop.shiny.service.OrderService;
import com.skyg0d.shop.shiny.util.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AuthUtils authUtils;

    @GetMapping
    @IsAdmin
    @Operation(summary = "Returns all orders with pagination", tags = "Orders")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Page<OrderResponse>> listAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(orderService.listAll(pageable));
    }

    @GetMapping("/my")
    @Operation(summary = "Returns all orders of an user with pagination", tags = "Orders")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Page<OrderResponse>> listAllByUser(@ParameterObject Pageable pageable) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(orderService.listAllByUser(pageable, userDetails.getEmail()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Returns order by id", tags = "Orders")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "When category not found"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<OrderResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(orderService.findByIdMapped(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Returns all searched orders with pagination", tags = "Orders")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Page<OrderResponse>> search(@ParameterObject OrderParameterSearch search, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(orderService.search(search, pageable));
    }

    @GetMapping("/my/search")
    @Operation(summary = "Returns all searched orders of an user with pagination", tags = "Orders")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Page<OrderResponse>> mySearch(@ParameterObject OrderParameterSearch search, @ParameterObject Pageable pageable) {
        UserDetailsImpl userDetails = authUtils.getUserDetails();

        search.setUserEmail(userDetails.getEmail());

        return ResponseEntity.ok(orderService.search(search, pageable));
    }

    @PostMapping
    @Operation(summary = "Persists a new order", tags = "Orders")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest request) {
        return new ResponseEntity<>(orderService.create(request, authUtils.getUserDetails().getEmail()), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Updates order status to canceled", tags = "Orders")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<MessageResponse> cancelOrder(@PathVariable String id) {
        orderService.cancelOrder(id);

        return ResponseEntity.ok(new MessageResponse("Order canceled"));
    }

    @PatchMapping("/{id}/ship")
    @IsAdmin
    @Operation(summary = "Updates order status to shipped", tags = "Orders")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<MessageResponse> shipOrder(@PathVariable String id) {
        orderService.adminChangeStatus(id, EOrderStatus.SHIPPED, "Order canceled, could not ship.");

        return ResponseEntity.ok(new MessageResponse("Order shipped"));
    }

    @PatchMapping("/{id}/otw")
    @IsAdmin
    @Operation(summary = "Updates order status to on the way", tags = "Orders")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<MessageResponse> onTheWayOrder(@PathVariable String id) {
        orderService.adminChangeStatus(id, EOrderStatus.ON_THE_WAY, "Order canceled, could not ship.");

        return ResponseEntity.ok(new MessageResponse("Order on the way"));
    }

    @PatchMapping("/{id}/deliver")
    @IsAdmin
    @Operation(summary = "Updates order status to delivered", tags = "Orders")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<MessageResponse> deliverOrder(@PathVariable String id) {
        orderService.adminChangeStatus(id, EOrderStatus.DELIVERED, "Order canceled, could not deliver.");

        return ResponseEntity.ok(new MessageResponse("Order delivered"));
    }

}
