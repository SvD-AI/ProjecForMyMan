package com.glovodelivery.project.controller;

import com.glovodelivery.project.dto.request.AdminCreateRestaurantRequest;
import com.glovodelivery.project.dto.request.AdminUpdateRestaurantRequest;
import com.glovodelivery.project.entity.Restaurant;
import com.glovodelivery.project.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

  private final RestaurantService restaurantService;

  @GetMapping
  @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_CUSTOMER')")
  public ResponseEntity<List<Restaurant>> getAllRestaurants() {
    log.info("Fetching all restaurants");
    return ResponseEntity.ok(restaurantService.getAllRestaurants());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
  public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
    log.info("Fetching restaurant by id: {}", id);
    return restaurantService.getRestaurantById(id)
      .map(restaurant -> {
        log.info("Found restaurant with id: {}", id);
        return ResponseEntity.ok(restaurant);
      })
      .orElseGet(() -> {
        log.warn("Restaurant with id {} not found", id);
        return ResponseEntity.notFound().build();
      });
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
  public ResponseEntity<Restaurant> createRestaurant(@RequestBody @Valid AdminCreateRestaurantRequest request) {
    Restaurant created = restaurantService.createRestaurant(request);
    log.info("Created new restaurant with id: {}", created.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
  public ResponseEntity<Restaurant> updateRestaurant(@PathVariable Long id,
                                                     @RequestBody @Valid AdminUpdateRestaurantRequest request) {
    Restaurant updated = restaurantService.updateRestaurant(id, request);
    log.info("Updated restaurant with id: {}", id);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
  public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
    restaurantService.deleteRestaurant(id);
    log.info("Deleted restaurant with id: {}", id);
    return ResponseEntity.noContent().build();
  }
}
