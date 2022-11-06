package com.skyg0d.shop.shiny.controller;

import com.skyg0d.shop.shiny.annotations.IsAdmin;
import com.skyg0d.shop.shiny.model.EOrderStatus;
import com.skyg0d.shop.shiny.payload.request.CreateOrderRequest;
import com.skyg0d.shop.shiny.payload.response.MessageResponse;
import com.skyg0d.shop.shiny.payload.response.OrderResponse;
import com.skyg0d.shop.shiny.security.service.UserDetailsImpl;
import com.skyg0d.shop.shiny.service.OrderService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    @IsAdmin
    public ResponseEntity<Page<OrderResponse>> listAll(Pageable pageable) {
        return ResponseEntity.ok(orderService.listAll(pageable));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<OrderResponse>> listAllByUser(Pageable pageable) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(orderService.listAllByUser(pageable, userDetails.getEmail()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(orderService.findByIdMapped(id));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest request) {
        return new ResponseEntity<>(orderService.create(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<MessageResponse> cancelOrder(@PathVariable String id) {
        orderService.cancelOrder(id);

        return ResponseEntity.ok(new MessageResponse("Order canceled"));
    }

    @PatchMapping("/{id}/ship")
    @IsAdmin
    public ResponseEntity<MessageResponse> shipOrder(@PathVariable String id) {
        orderService.adminChangeStatus(id, EOrderStatus.SHIPPED, "Order canceled, could not ship.");

        return ResponseEntity.ok(new MessageResponse("Order shipped"));
    }

    @PatchMapping("/{id}/otw")
    @IsAdmin
    public ResponseEntity<MessageResponse> onTheWayOrder(@PathVariable String id) {
        orderService.adminChangeStatus(id, EOrderStatus.ON_THE_WAY, "Order canceled, could not ship.");

        return ResponseEntity.ok(new MessageResponse("Order on the way"));
    }

    @PatchMapping("/{id}/deliver")
    @IsAdmin
    public ResponseEntity<MessageResponse> deliverOrder(@PathVariable String id) {
        orderService.adminChangeStatus(id, EOrderStatus.DELIVERED, "Order canceled, could not deliver.");

        return ResponseEntity.ok(new MessageResponse("Order delivered"));
    }

}
