package com.example.order.service;

import com.example.order.dto.OrderItemDTO;
import com.example.order.dto.OrderResponse;
import com.example.order.model.*;
import com.example.order.respository.OrderRepository;
import com.example.order.model.OrderStatus;
import com.example.order.model.CartItem;
import com.example.order.model.Order;
import com.example.order.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    CartItemService cartItemService;

    @Autowired
    OrderRepository orderRepository;

    public Optional<OrderResponse> createOrder(String userId) {

        List<CartItem> cartItems = cartItemService.getCart(userId);

        if (cartItems.isEmpty()) {
            return Optional.empty();
        }

        BigDecimal totalPrice = cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);
        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> new OrderItem(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice(),
                        order
                )).toList();
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        cartItemService.clearCart(userId);

        return Optional.of(mapToOrderResponse(savedOrder));
    }

    private OrderResponse mapToOrderResponse(Order savedOrder) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(savedOrder.getId());
        orderResponse.setStatus(savedOrder.getStatus());
        orderResponse.setTotalAmount(savedOrder.getTotalAmount());
        orderResponse.setItems(savedOrder.getItems().stream()
                .map(orderItem -> new OrderItemDTO(
                        orderItem.getId(),
                        orderItem.getProductId(),
                        orderItem.getQuantity(),
                        orderItem.getPrice(),
                        orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))
                )).toList());
        orderResponse.setCreatedAt(savedOrder.getCreatedAt());

        return orderResponse;
    }
}
