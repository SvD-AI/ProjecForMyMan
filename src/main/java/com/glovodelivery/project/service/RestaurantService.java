package com.glovodelivery.project.service;

import com.glovodelivery.project.dto.request.AdminCreateRestaurantRequest;
import com.glovodelivery.project.dto.request.AdminUpdateRestaurantRequest;
import com.glovodelivery.project.entity.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantService {
    List<Restaurant> getAllRestaurants();
    Optional<Restaurant> getRestaurantById(Long id);
    Restaurant createRestaurant(AdminCreateRestaurantRequest request);
    Restaurant updateRestaurant(Long id, AdminUpdateRestaurantRequest request);
    void deleteRestaurant(Long id);
}
