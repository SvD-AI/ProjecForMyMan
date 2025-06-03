package com.glovodelivery.project.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminUpdateRestaurantRequest(
    @NotNull(message = "Name is required")
    @Size(min = 1, max = 255)
    String name,

    @NotNull(message = "City is required")
    @Size(min = 1, max = 255)
    String city,

    Double rating
) {}
