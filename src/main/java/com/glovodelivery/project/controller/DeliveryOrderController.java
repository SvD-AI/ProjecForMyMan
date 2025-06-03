package com.glovodelivery.project.controller;

import com.glovodelivery.project.dto.request.AdminCreateDeliveryOrderRequest;
import com.glovodelivery.project.dto.request.AdminUpdateDeliveryOrderRequest;
import com.glovodelivery.project.dto.response.DeliveryOrderResponse;
import com.glovodelivery.project.service.DeliveryOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class DeliveryOrderController {

  private final DeliveryOrderService deliveryOrderService;

  @GetMapping
  @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
  public ResponseEntity<List<DeliveryOrderResponse>> getAllOrders() {
    log.info("getAllOrders called");
    List<DeliveryOrderResponse> orders = deliveryOrderService.getAllOrders();
    log.info("getAllOrders returning {} orders", orders.size());
    return ResponseEntity.ok(orders);
  }

  @GetMapping("/current-user")
  @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_COURIER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
  public ResponseEntity<List<DeliveryOrderResponse>> getAllOrdersForCurrentUser() {
    log.info("getAllOrdersForCurrentUser called");
    List<DeliveryOrderResponse> orders = deliveryOrderService.getOrdersForCurrentUser();
    log.info("getAllOrdersForCurrentUser returning {} orders", orders.size());
    return ResponseEntity.ok(orders);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
  public ResponseEntity<DeliveryOrderResponse> getOrderById(@PathVariable Long id) {
    log.info("getOrderById called with id: {}", id);
    Optional<DeliveryOrderResponse> order = deliveryOrderService.getOrderById(id);
    if (order.isPresent()) {
      log.info("Order found with id: {}", id);
      return ResponseEntity.ok(order.get());
    } else {
      log.warn("Order not found with id: {}", id);
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_CUSTOMER')")
  public ResponseEntity<DeliveryOrderResponse> createOrder(@RequestBody @Valid AdminCreateDeliveryOrderRequest request) {
    log.info("createOrder called with request: {}", request);
    DeliveryOrderResponse created = deliveryOrderService.createOrder(request);
    log.info("Order created with id: {}", created.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
  public ResponseEntity<DeliveryOrderResponse> updateOrder(@PathVariable Long id,
                                                           @RequestBody @Valid AdminUpdateDeliveryOrderRequest request) {
    log.info("updateOrder called for id: {}", id);
    DeliveryOrderResponse updated = deliveryOrderService.updateOrder(id, request);
    log.info("Order updated with id: {}", id);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
    log.info("deleteOrder called for id: {}", id);
    deliveryOrderService.deleteOrder(id);
    log.info("Order deleted with id: {}", id);
    return ResponseEntity.noContent().build();
  }
}
