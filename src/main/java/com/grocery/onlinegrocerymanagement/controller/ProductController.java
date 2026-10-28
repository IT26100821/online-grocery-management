
package com.grocery.onlinegrocerymanagement.controller;

import com.grocery.onlinegrocerymanagement.model.Product;
import com.grocery.onlinegrocerymanagement.service.ProductService;
import com.grocery.onlinegrocerymanagement.service.ProductImageService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final ProductImageService productImageService;

    // Constructor
    public ProductController(
            ProductService productService,
            ProductImageService productImageService) {

        this.productService = productService;
        this.productImageService = productImageService;
    }

    // READ - View All Products
    @GetMapping
    public String viewProducts(Model model) {

        model.addAttribute(
                "products",
                productService.getAllProducts()
        );

        return "products";
    }

    // CREATE - Add New Product With Optional Photo
    @PostMapping("/add")
    public String addProduct(
            @ModelAttribute Product product,
            @RequestParam(value = "image", required = false)
            MultipartFile image,
            RedirectAttributes redirectAttributes) {

        // Validate product details
        if (!isValidProduct(product)) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Invalid product details"
            );

            return "redirect:/products";
        }

        try {

            // Do not trust image filenames from form fields
            product.setImageFileName(null);

            // Upload image and get generated filename
            String imageFileName =
                    productImageService.saveProductImage(image);

            product.setImageFileName(imageFileName);

            // Save product with image filename
            productService.addProduct(product);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Product added successfully!"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/products";
    }

    // READ - Open Edit Product Page
    @GetMapping("/edit/{id}")
    public String editProduct(
            @PathVariable int id,
            Model model) {

        Product product =
                productService.getProductById(id);

        if (product == null) {
            return "redirect:/products";
        }

        model.addAttribute("product", product);

        return "edit-product";
    }

    // UPDATE - Edit Product With Optional New Photo
    @PostMapping("/update/{id}")
    public String updateProduct(
            @PathVariable int id,
            @ModelAttribute Product product,
            @RequestParam(value = "image", required = false)
            MultipartFile image,
            RedirectAttributes redirectAttributes) {

        if (productService.getProductById(id) == null) {
            return "redirect:/products";
        }

        if (!isValidProduct(product)) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Invalid product details"
            );

            return "redirect:/products/edit/" + id;
        }

        try {

            // Blank means retain old photo in ProductService
            product.setImageFileName(null);

            // Upload new photo only if selected
            String imageFileName =
                    productImageService.saveProductImage(image);

            product.setImageFileName(imageFileName);

            // Update product
            productService.updateProduct(id, product);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Product updated successfully!"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );

            return "redirect:/products/edit/" + id;
        }

        return "redirect:/products";
    }

    // DELETE - Delete Product
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id) {

        productService.deleteProduct(id);

        return "redirect:/products";
    }

    // VALIDATION
    private boolean isValidProduct(Product product) {

        return product.getName() != null
                && !product.getName().isBlank()
                && product.getCategory() != null
                && !product.getCategory().isBlank()
                && Double.isFinite(product.getPrice())
                && product.getPrice() >= 0
                && product.getQuantity() >= 0;
    }
}
