package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    private void mapRequestToProduct(Product product, ProductRequest productRequest) {
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCategory(productRequest.getCategory());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setImageUrl(productRequest.getImageUrl());
        product.setActive(productRequest.getActive());
    }

    private ProductResponse mapProductToResponse(Product product) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setName(product.getName());
        productResponse.setDescription(product.getDescription());
        productResponse.setPrice(product.getPrice());
        productResponse.setCategory(product.getCategory());
        productResponse.setActive(product.getActive());
        productResponse.setImageUrl(product.getImageUrl());
        productResponse.setStockQuantity(product.getStockQuantity());

        return productResponse;
    }

    public void createProduct(ProductRequest productRequest) {
        Product product = new Product();
        mapRequestToProduct(product, productRequest);
        productRepository.save(product);
    }

    public boolean updateProduct(Long id, ProductRequest updatedProduct) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    mapRequestToProduct(existingProduct, updatedProduct);
                    productRepository.save(existingProduct);
                    return true;
                })
                .orElse(false);
    }

    public List<ProductResponse> fetchAllProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(this::mapProductToResponse)
                .toList();
    }

    public boolean deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setActive(false);
                    productRepository.save(product);
                    return true;
                }).orElse(false);
    }

    public List<ProductResponse> searchProducts(String keyword) {
        System.out.println(keyword);
        return productRepository.searchproduct(keyword).stream()
                .map(this::mapProductToResponse)
                .toList();
    }
}
