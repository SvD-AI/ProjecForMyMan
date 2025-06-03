package com.glovodelivery.project.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record AdminUpdateDeliveryOrderRequest(
    @NotNull(message = "Customer name is required")
    @Size(min = 1, max = 255)
    String customerName,

    @NotNull(message = "Address is required")
    @Size(min = 1, max = 500)
    String address,

    @NotNull(message = "Restaurant ID is required")
    Long restaurantId,

    Long courierId,

    Double totalPrice,

    @NotNull(message = "Order items are required")
    List<OrderItemRequest> orderItems
) {}
