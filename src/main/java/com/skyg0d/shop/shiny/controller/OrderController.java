package com.skyg0d.shop.shiny.controller;

import com.skyg0d.shop.shiny.model.EOrderStatus;
import com.skyg0d.shop.shiny.payload.request.CreateOrderRequest;
import com.skyg0d.shop.shiny.payload.response.MessageResponse;
import com.skyg0d.shop.shiny.payload.response.OrderResponse;
import com.skyg0d.shop.shiny.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> listAll(Pageable pageable) {
        return ResponseEntity.ok(orderService.listAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(orderService.findByIdMapped(id));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest request) {
        return new ResponseEntity<>(orderService.create(request), HttpStatus.CREATED);
    }

    @PatchMapping("/update/{id}/status/{status}")
    public ResponseEntity<MessageResponse> updateStatus(@PathVariable String id, @PathVariable EOrderStatus status) {

        orderService.updateStatus(id, status);

        return ResponseEntity.ok(new MessageResponse("Order status updated"));
    }

}
