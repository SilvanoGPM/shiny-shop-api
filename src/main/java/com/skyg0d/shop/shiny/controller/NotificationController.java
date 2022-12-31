package com.skyg0d.shop.shiny.controller;

import com.skyg0d.shop.shiny.annotations.IsStaff;
import com.skyg0d.shop.shiny.annotations.IsUser;
import com.skyg0d.shop.shiny.payload.request.CreateNotificationRequest;
import com.skyg0d.shop.shiny.payload.request.CreateNotificationToAllRequest;
import com.skyg0d.shop.shiny.payload.response.CountNotificationsResponse;
import com.skyg0d.shop.shiny.payload.response.NotificationResponse;
import com.skyg0d.shop.shiny.payload.search.NotificationParameterSearch;
import com.skyg0d.shop.shiny.security.service.UserDetailsImpl;
import com.skyg0d.shop.shiny.service.NotificationService;
import com.skyg0d.shop.shiny.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    private final AuthUtils authUtils;

    @GetMapping("/unread")
    @IsUser
    public ResponseEntity<Page<NotificationResponse>> listAllByUserUnread(@ParameterObject Pageable pageable) {
        UserDetailsImpl userDetails = authUtils.getUserDetails();

        return ResponseEntity.ok(notificationService.listAllByUserUnread(pageable, userDetails.getEmail()));
    }

    @GetMapping("/read")
    @IsUser
    public ResponseEntity<Page<NotificationResponse>> listAllByUserRead(@ParameterObject Pageable pageable) {
        UserDetailsImpl userDetails = authUtils.getUserDetails();

        return ResponseEntity.ok(notificationService.listAllByUserRead(pageable, userDetails.getEmail()));
    }

    @GetMapping("/search")
    @IsStaff
    public ResponseEntity<Page<NotificationResponse>> search(@ParameterObject Pageable pageable, @ParameterObject NotificationParameterSearch search) {
        return ResponseEntity.ok(notificationService.search(pageable, search));
    }

    @GetMapping("/count")
    @IsUser
    public ResponseEntity<CountNotificationsResponse> countAllByUser() {
        UserDetailsImpl userDetails = authUtils.getUserDetails();

        return ResponseEntity.ok(notificationService.countAllByUser(userDetails.getEmail()));
    }

    @PostMapping
    @IsStaff
    public ResponseEntity<NotificationResponse> create(@RequestBody @Valid CreateNotificationRequest request) {
        return new ResponseEntity<>(notificationService.create(request), HttpStatus.CREATED);
    }

    @PostMapping("/all")
    @IsStaff
    public ResponseEntity<Void> createToAll(@RequestBody @Valid CreateNotificationToAllRequest request) {
        notificationService.createToAllUsers(request);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/read")
    @IsUser
    public ResponseEntity<Void> read(@PathVariable String id) {
        notificationService.read(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}/unread")
    @IsUser
    public ResponseEntity<Void> unread(@PathVariable String id) {
        notificationService.unread(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}/cancel")
    @IsStaff
    public ResponseEntity<Void> cancel(@PathVariable String id) {
        notificationService.cancel(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
