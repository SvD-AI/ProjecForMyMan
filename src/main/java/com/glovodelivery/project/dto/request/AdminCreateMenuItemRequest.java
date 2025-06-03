package com.glovodelivery.project.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminCreateMenuItemRequest(
    @NotNull(message = "Restaurant ID is required")
    Long restaurantId,

    @NotNull(message = "Name is required")
    @Size(min = 1, max = 255)
    String name,

    Double price
) {}
