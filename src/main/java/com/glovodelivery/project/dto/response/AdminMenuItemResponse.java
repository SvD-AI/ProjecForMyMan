package com.glovodelivery.project.dto.response;

public record AdminMenuItemResponse(
    Long id,
    String name,
    Double price,
    Long restaurantId,
    String restaurantName
) {}
