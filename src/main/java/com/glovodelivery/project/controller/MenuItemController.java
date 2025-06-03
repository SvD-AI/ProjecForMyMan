package com.glovodelivery.project.controller;

import com.glovodelivery.project.dto.request.AdminCreateMenuItemRequest;
import com.glovodelivery.project.dto.request.AdminUpdateMenuItemRequest;
import com.glovodelivery.project.entity.MenuItem;
import com.glovodelivery.project.service.MenuItemService;
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
@RequestMapping("/api/v1/admin/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

  private final MenuItemService menuItemService;

  @GetMapping
  @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_CUSTOMER')")
  public ResponseEntity<List<com.glovodelivery.project.dto.response.AdminMenuItemResponse>> getAllMenuItems() {
    log.info("Fetching all menu items with restaurant names");
    return ResponseEntity.ok(menuItemService.getAllMenuItemsWithRestaurantName());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
  public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Long id) {
    log.info("Fetching menu item by id: {}", id);
    return menuItemService.getMenuItemById(id)
      .map(menuItem -> {
        log.info("Found menu item with id: {}", id);
        return ResponseEntity.ok(menuItem);
      })
      .orElseGet(() -> {
        log.warn("Menu item with id {} not found", id);
        return ResponseEntity.notFound().build();
      });
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
  public ResponseEntity<MenuItem> createMenuItem(@RequestBody @Valid AdminCreateMenuItemRequest request) {
    MenuItem created = menuItemService.createMenuItem(request);
    log.info("Created new menu item with id: {}", created.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
  public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Long id,
                                                 @RequestBody @Valid AdminUpdateMenuItemRequest request) {
    MenuItem updated = menuItemService.updateMenuItem(id, request);
    log.info("Updated menu item with id: {}", id);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
  public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
    menuItemService.deleteMenuItem(id);
    log.info("Deleted menu item with id: {}", id);
    return ResponseEntity.noContent().build();
  }
}
