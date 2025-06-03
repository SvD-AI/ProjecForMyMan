package com.glovodelivery.project.service.impl;

import com.glovodelivery.project.dto.request.AdminCreateDeliveryOrderRequest;
import com.glovodelivery.project.dto.request.AdminUpdateDeliveryOrderRequest;
import com.glovodelivery.project.dto.response.DeliveryOrderResponse;
import com.glovodelivery.project.entity.DeliveryOrder;
import com.glovodelivery.project.entity.MenuItem;
import com.glovodelivery.project.entity.OrderItem;
import com.glovodelivery.project.entity.OrderItemId;
import com.glovodelivery.project.entity.Restaurant;
import com.glovodelivery.project.entity.User;
import com.glovodelivery.project.repository.DeliveryOrderRepository;
import com.glovodelivery.project.repository.MenuItemRepository;
import com.glovodelivery.project.repository.RestaurantRepository;
import com.glovodelivery.project.repository.UserRepository;
import com.glovodelivery.project.service.DeliveryOrderService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryOrderServiceImpl implements DeliveryOrderService {

    private final DeliveryOrderRepository deliveryOrderRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;

    @Override
    public List<DeliveryOrderResponse> getAllOrders() {
        return deliveryOrderRepository.findAll()
                .stream()
                .map(DeliveryOrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryOrderResponse> getOrdersForCurrentUser() {
        User currentUser = getCurrentUser();

        if (hasRole(currentUser, com.glovodelivery.project.enums.RoleName.ROLE_ADMIN) ||
            hasRole(currentUser, com.glovodelivery.project.enums.RoleName.ROLE_MANAGER)) {
            return deliveryOrderRepository.findAll()
                    .stream()
                    .map(DeliveryOrderResponse::fromEntity)
                    .collect(Collectors.toList());
        }

        if (hasRole(currentUser, com.glovodelivery.project.enums.RoleName.ROLE_COURIER)) {
            return deliveryOrderRepository.findAllByCourierId(currentUser.getId())
                    .stream()
                    .map(DeliveryOrderResponse::fromEntity)
                    .collect(Collectors.toList());
        }

        if (hasRole(currentUser, com.glovodelivery.project.enums.RoleName.ROLE_CUSTOMER)) {
            return deliveryOrderRepository.findAllByCustomerId(currentUser.getId())
                    .stream()
                    .map(DeliveryOrderResponse::fromEntity)
                    .collect(Collectors.toList());
        }

        throw new AccessDeniedException("Access denied");

    }

    private User getCurrentUser() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"));
    }

    private boolean hasRole(User user, com.glovodelivery.project.enums.RoleName roleName) {
        return user.getRoles().stream().anyMatch(r -> r.getName().equals(roleName.name()));
    }

    @Override
    public Optional<DeliveryOrderResponse> getOrderById(Long id) {
        return deliveryOrderRepository.findById(id)
                .map(DeliveryOrderResponse::fromEntity);
    }

    @Override
    public DeliveryOrderResponse createOrder(AdminCreateDeliveryOrderRequest request) {
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        User courier = null;
        if (request.courierId() != null) {
            courier = userRepository.findById(request.courierId())
                    .orElseThrow(() -> new RuntimeException("Courier not found"));
        }

        User currentUser = getCurrentUser();

        DeliveryOrder order = new DeliveryOrder();
        order.setCustomerName(request.customerName());
        order.setAddress(request.address());
        order.setRestaurant(restaurant);
        order.setCourier(courier);
        order.setCustomer(currentUser);

        DeliveryOrder savedOrder = deliveryOrderRepository.save(order);

        for (var itemRequest : request.orderItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.menuItemId())
                    .orElseThrow(() -> new RuntimeException("MenuItem not found"));

            OrderItem orderItem = new OrderItem();

            OrderItemId orderItemId = new OrderItemId();
            orderItemId.setOrderId(savedOrder.getId());
            orderItemId.setMenuItemId(menuItem.getId());

            orderItem.setId(orderItemId);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemRequest.quantity());
            orderItem.setDeliveryOrder(savedOrder);

            savedOrder.getOrderItems().add(orderItem);
        }

        savedOrder = deliveryOrderRepository.save(savedOrder);

        return DeliveryOrderResponse.fromEntity(savedOrder);
    }

    @Override
    public DeliveryOrderResponse updateOrder(Long id, AdminUpdateDeliveryOrderRequest request) {
        User currentUser = getCurrentUser();
        return deliveryOrderRepository.findById(id)
                .map(order -> {
                    if (hasRole(currentUser, com.glovodelivery.project.enums.RoleName.ROLE_CUSTOMER) &&
                        !order.getCustomer().getId().equals(currentUser.getId())) {
                        throw new org.springframework.security.access.AccessDeniedException("You can only manage your own orders");
                    }

                    Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                            .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

                    User courier = null;
                    if (request.courierId() != null) {
                        courier = userRepository.findById(request.courierId())
                                .orElseThrow(() -> new IllegalArgumentException("Courier not found"));
                    }

                    order.setCustomerName(request.customerName());
                    order.setAddress(request.address());
                    order.setRestaurant(restaurant);
                    order.setCourier(courier);

                    if (request.orderItems() != null) {
                        for (var itemRequest : request.orderItems()) {
                            MenuItem menuItem = menuItemRepository.findById(itemRequest.menuItemId())
                                    .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + itemRequest.menuItemId()));
                            if (!menuItem.getRestaurant().getId().equals(restaurant.getId())) {
                                throw new IllegalArgumentException("All order items must belong to the selected restaurant");
                            }
                            if (itemRequest.quantity() < 1) {
                                throw new IllegalArgumentException("Quantity must be at least 1");
                            }
                        }

                        order.getOrderItems().clear();

                        for (var itemRequest : request.orderItems()) {
                            MenuItem menuItem = menuItemRepository.findById(itemRequest.menuItemId()).get();

                            OrderItem orderItem = new OrderItem();

                            OrderItemId orderItemId = new OrderItemId();
                            orderItemId.setOrderId(order.getId());
                            orderItemId.setMenuItemId(menuItem.getId());

                            orderItem.setId(orderItemId);
                            orderItem.setMenuItem(menuItem);
                            orderItem.setQuantity(itemRequest.quantity());
                            orderItem.setDeliveryOrder(order);

                            order.getOrderItems().add(orderItem);
                        }
                    }

                    DeliveryOrder savedOrder = deliveryOrderRepository.save(order);
                    return DeliveryOrderResponse.fromEntity(savedOrder);
                })
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    @Override
    public void deleteOrder(Long id) {
        User currentUser = getCurrentUser();
        DeliveryOrder order = deliveryOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (hasRole(currentUser, com.glovodelivery.project.enums.RoleName.ROLE_CUSTOMER) &&
            !order.getCustomer().getId().equals(currentUser.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("You can only manage your own orders");
        }

        deliveryOrderRepository.deleteById(id);
    }

}
