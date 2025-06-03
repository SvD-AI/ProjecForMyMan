package com.glovodelivery.project.controller;

import com.glovodelivery.project.dto.response.AdminMenuItemResponse;
import com.glovodelivery.project.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class DishController {

  private final MenuItemService menuItemService;

  @GetMapping("/dishes")
  @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_CUSTOMER')")
  public ResponseEntity<List<AdminMenuItemResponse>> getAllDishes(@RequestParam(required = false) Long restaurantId) {
    log.info("Fetching dishes, restaurantId = {}", restaurantId);
    List<AdminMenuItemResponse> dishes;
    if (restaurantId != null) {
      dishes = menuItemService.getMenuItemsByRestaurantId(restaurantId);
      log.info("Fetched {} dishes for restaurantId {}", dishes.size(), restaurantId);
    } else {
      dishes = menuItemService.getAllMenuItemsWithRestaurantName();
      log.info("Fetched all dishes, total count: {}", dishes.size());
    }
    return ResponseEntity.ok(dishes);
  }
}
