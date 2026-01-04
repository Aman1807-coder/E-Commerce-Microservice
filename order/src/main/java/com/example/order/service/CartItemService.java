package com.example.order.service;

import com.example.order.client.ProductServiceClient;
import com.example.order.client.UserServiceClient;
import com.example.order.dto.CartItemRequest;
import com.example.order.dto.ProductResponse;
import com.example.order.dto.UserResponse;
import com.example.order.model.CartItem;
import com.example.order.respository.CartItemRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartItemService {

    @Autowired
    ProductServiceClient productServiceClient;

    @Autowired
    UserServiceClient userServiceClient;

    @Autowired
    CartItemRepository cartItemRepository;

    public List<CartItem> getCart(String userId) {
        return cartItemRepository.findAllByUserId(userId);
    }


    @CircuitBreaker(name = "productService", fallbackMethod = "addToCartFallback")
    public boolean addToCart(String userId, CartItemRequest cartItemRequest) {

        ProductResponse productResponse = productServiceClient.getProductDetails(cartItemRequest.getProductId());
        if (productResponse == null || productResponse.getStockQuantity() < cartItemRequest.getQuantity()) return false;

        UserResponse userResponse = userServiceClient.getUserDetails(userId);

        if (userResponse == null) return false;

        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(userId, cartItemRequest.getProductId());

        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemRequest.getQuantity());
            existingCartItem.setPrice(productResponse.getPrice());
            cartItemRepository.save(existingCartItem);
        }

        else {
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(cartItemRequest.getProductId());
            cartItem.setQuantity(cartItemRequest.getQuantity());
            cartItem.setPrice(productResponse.getPrice());

            cartItemRepository.save(cartItem);
        }

        return true;
    }

    public boolean deleteItemFromCart(String userId, String productId) {

        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);

        if (cartItem != null) {
            cartItemRepository.delete(cartItem);
            return true;
        }

        return false;
    }

    public boolean addToCartFallback(String userId,
                                     CartItemRequest cartItemRequest,
                                     Exception exception) {

        System.out.println("FALLBACK CALLED : " + exception);
        return false;
    }

    public void clearCart(String userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}
