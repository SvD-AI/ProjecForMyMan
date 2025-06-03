package com.glovodelivery.project.service;

import com.glovodelivery.project.dto.request.AdminCreateDeliveryOrderRequest;
import com.glovodelivery.project.dto.request.AdminUpdateDeliveryOrderRequest;
import com.glovodelivery.project.dto.response.DeliveryOrderResponse;

import java.util.List;
import java.util.Optional;

public interface DeliveryOrderService {
    List<DeliveryOrderResponse> getAllOrders();
    Optional<DeliveryOrderResponse> getOrderById(Long id);
    DeliveryOrderResponse createOrder(AdminCreateDeliveryOrderRequest request);
    DeliveryOrderResponse updateOrder(Long id, AdminUpdateDeliveryOrderRequest request);
    void deleteOrder(Long id);

    List<DeliveryOrderResponse> getOrdersForCurrentUser();
}
