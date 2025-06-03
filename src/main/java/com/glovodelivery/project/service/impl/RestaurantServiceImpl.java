package com.glovodelivery.project.service.impl;

import com.glovodelivery.project.dto.request.AdminCreateRestaurantRequest;
import com.glovodelivery.project.dto.request.AdminUpdateRestaurantRequest;
import com.glovodelivery.project.entity.Restaurant;
import com.glovodelivery.project.repository.RestaurantRepository;
import com.glovodelivery.project.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @Override
    public Optional<Restaurant> getRestaurantById(Long id) {
        return restaurantRepository.findById(id);
    }

    @Override
    public Restaurant createRestaurant(AdminCreateRestaurantRequest request) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.name());
        restaurant.setCity(request.city());
        restaurant.setRating(request.rating());
        return restaurantRepository.save(restaurant);
    }

    @Override
    public Restaurant updateRestaurant(Long id, AdminUpdateRestaurantRequest request) {
        return restaurantRepository.findById(id)
                .map(restaurant -> {
                    restaurant.setName(request.name());
                    restaurant.setCity(request.city());
                    restaurant.setRating(request.rating());
                    return restaurantRepository.save(restaurant);
                })
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
    }

    @Override
    public void deleteRestaurant(Long id) {
        restaurantRepository.deleteById(id);
    }
}
