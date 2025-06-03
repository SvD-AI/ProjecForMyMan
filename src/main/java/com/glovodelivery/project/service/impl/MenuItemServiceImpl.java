package com.glovodelivery.project.service.impl;

import com.glovodelivery.project.dto.request.AdminCreateMenuItemRequest;
import com.glovodelivery.project.dto.request.AdminUpdateMenuItemRequest;
import com.glovodelivery.project.dto.response.AdminMenuItemResponse;
import com.glovodelivery.project.entity.MenuItem;
import com.glovodelivery.project.entity.Restaurant;
import com.glovodelivery.project.repository.MenuItemRepository;
import com.glovodelivery.project.repository.RestaurantRepository;
import com.glovodelivery.project.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    @Override
    public List<AdminMenuItemResponse> getAllMenuItemsWithRestaurantName() {
        return menuItemRepository.findAll().stream()
            .map(item -> new AdminMenuItemResponse(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getRestaurant().getId(),
                item.getRestaurant().getName()
            ))
            .toList();
    }

    @Override
    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuItemRepository.findById(id);
    }

    @Override
    public MenuItem createMenuItem(AdminCreateMenuItemRequest request) {
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        MenuItem menuItem = new MenuItem();
        menuItem.setRestaurant(restaurant);
        menuItem.setName(request.name());
        menuItem.setPrice(request.price());
        return menuItemRepository.save(menuItem);
    }

    @Override
    public MenuItem updateMenuItem(Long id, AdminUpdateMenuItemRequest request) {
        return menuItemRepository.findById(id)
                .map(menuItem -> {
                    Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                            .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
                    menuItem.setRestaurant(restaurant);
                    menuItem.setName(request.name());
                    menuItem.setPrice(request.price());
                    return menuItemRepository.save(menuItem);
                })
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found"));
    }

    @Override
    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }

    @Override
    public List<MenuItem> getMenuItemsByRestaurantExcludingSelected(Long restaurantId, List<Long> excludedMenuItemIds) {
        List<MenuItem> menuItems = menuItemRepository.findByRestaurantId(restaurantId);
        if (excludedMenuItemIds == null || excludedMenuItemIds.isEmpty()) {
            return menuItems;
        }
        return menuItems.stream()
                .filter(menuItem -> !excludedMenuItemIds.contains(menuItem.getId()))
                .toList();
    }

    @Override
    public List<AdminMenuItemResponse> getMenuItemsByRestaurantId(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId).stream()
            .map(item -> new AdminMenuItemResponse(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getRestaurant().getId(),
                item.getRestaurant().getName()
            ))
            .toList();
    }
}
