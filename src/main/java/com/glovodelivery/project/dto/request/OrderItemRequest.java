package com.glovodelivery.project.dto.request;

import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
    @NotNull(message = "Menu item ID is required")
    Long menuItemId,

    @NotNull(message = "Quantity is required")
    Integer quantity
) {}
