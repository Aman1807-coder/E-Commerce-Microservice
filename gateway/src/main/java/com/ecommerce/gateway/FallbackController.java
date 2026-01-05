package com.ecommerce.gateway;


import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class FallbackController {

    @GetMapping("/fallback/product")
    public ResponseEntity<List<String>> productFallback() {
        return ResponseEntity.status(HttpStatus.SC_SERVICE_UNAVAILABLE)
                .body(Collections.singletonList("Product Service unavailable at this moment." +
                        "Please try later"));
    }

    @GetMapping("/fallback/user")
    public ResponseEntity<List<String>> userFallback() {
        return ResponseEntity.status(HttpStatus.SC_SERVICE_UNAVAILABLE)
                .body(Collections.singletonList("User Service unavailable at this moment." +
                        "Please try later"));
    }

    @GetMapping("/fallback/order")
    public ResponseEntity<List<String>> orderFallback() {
        return ResponseEntity.status(HttpStatus.SC_SERVICE_UNAVAILABLE)
                .body(Collections.singletonList("Order Service unavailable at this moment." +
                        "Please try later"));
    }
}
