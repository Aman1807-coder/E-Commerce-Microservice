package com.example.order.service;

import com.example.order.dto.CartItemRequest;
import com.example.order.model.CartItem;
import com.example.order.respository.CartItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartItemService {

//    @Autowired
//    ProductRepository productRepository;

//    @Autowired
//    UserRepository userRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    public List<CartItem> getCart(String userId) {
        return cartItemRepository.findAllByUserId(userId);
    }

    public boolean addToCart(String userId, CartItemRequest cartItemRequest) {

//        Optional<Product> productOpt =  productRepository.findById(cartItemRequest.getProductId());
//
//        if (productOpt.isEmpty()) return false;
//        Product product = productOpt.get();
//        if (product.getStockQuantity() < cartItemRequest.getQuantity()) return false;
//
//        Optional<User> userOpt =  userRepository.findById(Long.valueOf(userId));
//
//        if (userOpt.isEmpty()) return false;
//        User user = userOpt.get();

        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(userId, cartItemRequest.getProductId());

        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemRequest.getQuantity());
            existingCartItem.setPrice(BigDecimal.valueOf(1000.00));
            cartItemRepository.save(existingCartItem);
        }

        else {
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(cartItemRequest.getProductId());
            cartItem.setQuantity(cartItemRequest.getQuantity());
            cartItem.setPrice(BigDecimal.valueOf(1000.00));

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

    public void clearCart(String userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}
