package com.grocery.onlinegrocerymanagement.service;

import com.grocery.onlinegrocerymanagement.model.Product;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final Path filePath = Paths.get("data", "products.txt");

    // READ - View All Products
    public synchronized List<Product> getAllProducts() {
        return loadProducts();
    }

    // CREATE - Add New Product
    public synchronized void addProduct(Product product) {

        List<Product> products = loadProducts();

        int newId = products.stream()
                .mapToInt(Product::getId)
                .max()
                .orElse(0) + 1;

        product.setId(newId);

        products.add(product);

        saveProducts(products);
    }

    // READ - Find Product By ID
    public synchronized Product getProductById(int id) {

        for (Product product : loadProducts()) {

            if (product.getId() == id) {
                return product;
            }
        }

        return null;
    }

    // UPDATE - Edit Product
    public synchronized void updateProduct(int id, Product updatedProduct) {

        List<Product> products = loadProducts();

        for (int i = 0; i < products.size(); i++) {

            if (products.get(i).getId() == id) {

                updatedProduct.setId(id);

                products.set(i, updatedProduct);

                saveProducts(products);
                return;
            }
        }

        throw new IllegalArgumentException("Product not found");
    }

    // DELETE - Delete Product
    public synchronized void deleteProduct(int id) {

        List<Product> products = loadProducts();

        boolean removed = products.removeIf(
                product -> product.getId() == id
        );

        if (removed) {
            saveProducts(products);
        }
    }

    // Load Products From Text File
    private List<Product> loadProducts() {

        List<Product> products = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return products;
        }

        try {

            List<String> lines = Files.readAllLines(
                    filePath, StandardCharsets.UTF_8
            );

            for (String line : lines) {

                if (line.isBlank()) {
                    continue;
                }

                String[] data = line.split("\\|", -1);

                if (data.length != 5) {
                    throw new IllegalStateException(
                            "Invalid product data: " + line
                    );
                }

                Product product = new Product(
                        Integer.parseInt(data[0]),
                        data[1],
                        data[2],
                        Double.parseDouble(data[3]),
                        Integer.parseInt(data[4])
                );

                products.add(product);
            }

        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Error reading products file", e
            );
        }

        return products;
    }

    // Save Products To Text File
    private void saveProducts(List<Product> products) {

        try {

            Files.createDirectories(filePath.getParent());

            List<String> lines = new ArrayList<>();

            for (Product product : products) {

                String line =
                        product.getId() + "|" +
                                clean(product.getName()) + "|" +
                                clean(product.getCategory()) + "|" +
                                product.getPrice() + "|" +
                                product.getQuantity();

                lines.add(line);
            }

            Files.write(
                    filePath,
                    lines,
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Error saving products file", e
            );
        }
    }

    // Prevent separator characters in saved fields
    private String clean(String value) {

        if (value == null) {
            return "";
        }

        return value.replace("|", " ")
                .replace("\n", " ")
                .replace("\r", " ");
    }
}

