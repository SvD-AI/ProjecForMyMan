package com.glovodelivery.project.service;

import com.glovodelivery.project.dto.request.AdminCreateMenuItemRequest;
import com.glovodelivery.project.dto.request.AdminUpdateMenuItemRequest;
import com.glovodelivery.project.dto.response.AdminMenuItemResponse;
import com.glovodelivery.project.entity.MenuItem;

import java.util.List;
import java.util.Optional;

public interface MenuItemService {
    List<MenuItem> getAllMenuItems();
    List<AdminMenuItemResponse> getAllMenuItemsWithRestaurantName();
    Optional<MenuItem> getMenuItemById(Long id);
    MenuItem createMenuItem(AdminCreateMenuItemRequest request);
    MenuItem updateMenuItem(Long id, AdminUpdateMenuItemRequest request);
    void deleteMenuItem(Long id);

    List<MenuItem> getMenuItemsByRestaurantExcludingSelected(Long restaurantId, List<Long> excludedMenuItemIds);

    List<AdminMenuItemResponse> getMenuItemsByRestaurantId(Long restaurantId);
}
