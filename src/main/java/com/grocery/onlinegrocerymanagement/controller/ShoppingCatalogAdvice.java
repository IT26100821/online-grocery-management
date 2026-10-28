
package com.grocery.onlinegrocerymanagement.controller;

import com.grocery.onlinegrocerymanagement.model.Product;
import com.grocery.onlinegrocerymanagement.service.ProductService;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice(assignableTypes = HomeController.class)
public class ShoppingCatalogAdvice {

    private final ProductService productService;

    public ShoppingCatalogAdvice(ProductService productService) {
        this.productService = productService;
    }

    // Supply actual products to the Home Page
    @ModelAttribute("shopProducts")
    public List<Product> loadShopProducts() {
        return productService.getAllProducts();
    }
}
