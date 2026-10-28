
package com.grocery.onlinegrocerymanagement.controller;

import com.grocery.onlinegrocerymanagement.service.ProductImageService;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class ProductImageController {

    private final ProductImageService productImageService;

    public ProductImageController(
            ProductImageService productImageService) {

        this.productImageService = productImageService;
    }

    // Display a saved product image
    @GetMapping("/product-images/{fileName}")
    public ResponseEntity<Resource> getProductImage(
            @PathVariable String fileName) {

        try {
            Path imagePath =
                    productImageService.getImagePath(fileName);

            if (!Files.isRegularFile(imagePath)
                    || !Files.isReadable(imagePath)) {

                return ResponseEntity.notFound().build();
            }

            MediaType imageType =
                    fileName.endsWith(".png")
                            ? MediaType.IMAGE_PNG
                            : MediaType.IMAGE_JPEG;

            Resource resource =
                    new FileSystemResource(imagePath);

            return ResponseEntity.ok()
                    .contentType(imageType)
                    .body(resource);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.notFound().build();
        }
    }
}
