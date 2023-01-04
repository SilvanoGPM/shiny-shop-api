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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Returns all unread notifications with pagination", tags = "Notifications")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Page<NotificationResponse>> listAllByUserUnread(@ParameterObject Pageable pageable) {
        UserDetailsImpl userDetails = authUtils.getUserDetails();

        return ResponseEntity.ok(notificationService.listAllByUserUnread(pageable, userDetails.getEmail()));
    }

    @GetMapping("/read")
    @IsUser
    @Operation(summary = "Returns all read notifications with pagination", tags = "Notifications")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Page<NotificationResponse>> listAllByUserRead(@ParameterObject Pageable pageable) {
        UserDetailsImpl userDetails = authUtils.getUserDetails();

        return ResponseEntity.ok(notificationService.listAllByUserRead(pageable, userDetails.getEmail()));
    }

    @GetMapping("/search")
    @IsStaff
    @Operation(summary = "Returns all searched notifications with pagination", tags = "Notifications")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Page<NotificationResponse>> search(@ParameterObject Pageable pageable, @ParameterObject NotificationParameterSearch search) {
        return ResponseEntity.ok(notificationService.search(pageable, search));
    }

    @GetMapping("/count")
    @IsUser
    @Operation(summary = "Count all unread notifications", tags = "Notifications")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<CountNotificationsResponse> countAllByUser() {
        UserDetailsImpl userDetails = authUtils.getUserDetails();

        return ResponseEntity.ok(notificationService.countAllByUser(userDetails.getEmail()));
    }

    @PostMapping
    @IsStaff
    @Operation(summary = "Persists a new notification", tags = "Notifications")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<NotificationResponse> create(@RequestBody @Valid CreateNotificationRequest request) {
        return new ResponseEntity<>(notificationService.create(request), HttpStatus.CREATED);
    }

    @PostMapping("/all")
    @IsStaff
    @Operation(summary = "Persists a new notification to all users", tags = "Notifications")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Void> createToAll(@RequestBody @Valid CreateNotificationToAllRequest request) {
        notificationService.createToAllUsers(request);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/read")
    @IsUser
    @Operation(summary = "Updates notification readAt to now", tags = "Notifications")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Void> read(@PathVariable String id) {
        notificationService.read(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}/unread")
    @IsUser
    @Operation(summary = "Updates notification readAt to null", tags = "Notifications")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Void> unread(@PathVariable String id) {
        notificationService.unread(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}/cancel")
    @IsStaff
    @Operation(summary = "Updates notification canceledAt to now", tags = "Notifications")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Void> cancel(@PathVariable String id) {
        notificationService.cancel(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
