package com.example.order.controller;

import com.example.order.dto.CartItemRequest;
import com.example.order.model.CartItem;
import com.example.order.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartItemController {

    @Autowired
    CartItemService cartItemService;

    @GetMapping
    public ResponseEntity<List<CartItem>> getCartItem(@RequestHeader("X-User-ID") String userId) {
        return new ResponseEntity<>(cartItemService.getCart(userId), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(
            @RequestHeader("X-User-ID") String userId,
            @RequestBody CartItemRequest cartItemRequest
            ) {

        if (!cartItemService.addToCart(userId, cartItemRequest)) {
            return ResponseEntity.badRequest().body("Product out of stock or user not found or product not found");
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<Void> deleteFromCart(
            @RequestHeader("X-User-ID") String userId,
            @PathVariable String productId
    ) {
        System.out.println(productId);
        return cartItemService.deleteItemFromCart(userId, productId) ? ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }
}
