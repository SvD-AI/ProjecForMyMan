package com.glovodelivery.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryOrderResponse {
    private Long id;
    private String customerName;
    private String address;
    private String restaurantName;
    private String courierName;
    private Double totalPrice;
    private List<OrderItemResponse> orderItems;

    private Long restaurantId;
    private Long courierId;

    @Data
    @NoArgsConstructor
    public static class OrderItemResponse {
        private Long menuItemId;
        private String name;
        private Integer quantity;

        public OrderItemResponse(Long menuItemId, String name, Integer quantity) {
            this.menuItemId = menuItemId;
            this.name = name;
            this.quantity = quantity;
        }
    }

    public static DeliveryOrderResponse fromEntity(com.glovodelivery.project.entity.DeliveryOrder order) {
        DeliveryOrderResponse response = new DeliveryOrderResponse();
        response.setId(order.getId());
        response.setCustomerName(order.getCustomerName());
        response.setAddress(order.getAddress());
        response.setRestaurantName(order.getRestaurant() != null ? order.getRestaurant().getName() : null);
        response.setCourierName(order.getCourier() != null ? order.getCourier().getFirstName() + " " + order.getCourier().getLastName() : null);
        response.setTotalPrice(order.calculateTotal());
        response.setRestaurantId(order.getRestaurant() != null ? order.getRestaurant().getId() : null);
        response.setCourierId(order.getCourier() != null ? order.getCourier().getId() : null);
        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getMenuItem().getId(),
                        item.getMenuItem().getName(),
                        item.getQuantity()))
                .collect(Collectors.toList());
        response.setOrderItems(items);
        return response;
    }
}
