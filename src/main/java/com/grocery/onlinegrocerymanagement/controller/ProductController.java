package com.grocery.onlinegrocerymanagement.controller;

import com.grocery.onlinegrocerymanagement.model.Product;
import com.grocery.onlinegrocerymanagement.service.ProductService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // READ - View All Products
    @GetMapping
    public String viewProducts(Model model) {

        model.addAttribute("products",
                productService.getAllProducts());

        return "products";
    }

    // CREATE - Add New Product
    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product) {

        if (product.getName() == null ||
                product.getName().isBlank() ||
                product.getCategory() == null ||
                product.getCategory().isBlank() ||
                !Double.isFinite(product.getPrice()) ||
                product.getPrice() < 0 ||
                product.getQuantity() < 0) {
            return "redirect:/products";
        }

        productService.addProduct(product);

        return "redirect:/products";
    }

    // Open Edit Product Page
    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable int id, Model model) {

        Product product = productService.getProductById(id);

        if (product == null) {
            return "redirect:/products";
        }

        model.addAttribute("product", product);

        return "edit-product";
    }

    // UPDATE - Update Product
    @PostMapping("/update/{id}")
    public String updateProduct(
            @PathVariable int id,
            @ModelAttribute Product product) {

        if (productService.getProductById(id) == null) {
            return "redirect:/products";
        }

        if (product.getName() == null ||
                product.getName().isBlank() ||
                product.getCategory() == null ||
                product.getCategory().isBlank() ||
                !Double.isFinite(product.getPrice()) ||
                product.getPrice() < 0 ||
                product.getQuantity() < 0) {
            return "redirect:/products/edit/" + id;
        }

        productService.updateProduct(id, product);

        return "redirect:/products";
    }

    // DELETE - Delete Product
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id) {

        productService.deleteProduct(id);

        return "redirect:/products";
    }
}
