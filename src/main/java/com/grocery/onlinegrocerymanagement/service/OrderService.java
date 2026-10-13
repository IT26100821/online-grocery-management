package com.grocery.onlinegrocerymanagement.service;

import com.grocery.onlinegrocerymanagement.model.Order;
import com.grocery.onlinegrocerymanagement.model.Product;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final Path filePath =
            Paths.get("data", "orders.txt");

    private final ProductService productService;
    private final CustomerService customerService;

    // Constructor
    public OrderService(ProductService productService,
                        CustomerService customerService) {

        this.productService = productService;
        this.customerService = customerService;
    }

    // CREATE - Add Order
    public synchronized void addOrder(Order order) {

        List<Order> orders = loadOrders();

        int newId = 1;

        for (Order o : orders) {
            if (o.getId() >= newId) {
                newId = o.getId() + 1;
            }
        }

        order.setId(newId);
        order.setStatus("Pending");

        calculateTotal(order);

        orders.add(order);

        saveOrders(orders);
    }

    // READ - View All Orders
    public synchronized List<Order> getAllOrders() {

        return loadOrders();
    }

    // READ - Find Order By ID
    public synchronized Order getOrderById(int id) {

        for (Order order : loadOrders()) {

            if (order.getId() == id) {
                return order;
            }
        }

        return null;
    }

    // UPDATE - Update Order
    public synchronized void updateOrder(
            int id, Order updatedOrder) {

        List<Order> orders = loadOrders();

        for (int i = 0; i < orders.size(); i++) {

            if (orders.get(i).getId() == id) {

                updatedOrder.setId(id);

                calculateTotal(updatedOrder);

                orders.set(i, updatedOrder);

                saveOrders(orders);

                return;
            }
        }

        throw new IllegalArgumentException(
                "Order not found"
        );
    }

    // DELETE - Delete Order
    public synchronized void deleteOrder(int id) {

        List<Order> orders = loadOrders();

        boolean removed = orders.removeIf(
                order -> order.getId() == id
        );

        if (removed) {
            saveOrders(orders);
        }
    }

    // Calculate Total Price
    private void calculateTotal(Order order) {

        if (order.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        if (customerService.getCustomerById(
                order.getCustomerId()) == null) {

            throw new IllegalArgumentException(
                    "Customer not found"
            );
        }

        Product product = productService.getProductById(
                order.getProductId()
        );

        if (product == null) {
            throw new IllegalArgumentException(
                    "Product not found"
            );
        }

        double total =
                product.getPrice() * order.getQuantity();

        order.setTotalPrice(total);

        if (order.getStatus() == null ||
                order.getStatus().isBlank()) {

            order.setStatus("Pending");
        }
    }

    // READ - Load Orders From File
    private List<Order> loadOrders() {

        List<Order> orders = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return orders;
        }

        try {

            List<String> lines =
                    Files.readAllLines(filePath);

            for (String line : lines) {

                if (line.isBlank()) {
                    continue;
                }

                String[] data = line.split("\\|", -1);

                if (data.length != 6) {
                    throw new IllegalStateException(
                            "Invalid order data"
                    );
                }

                Order order = new Order(
                        Integer.parseInt(data[0]),
                        Integer.parseInt(data[1]),
                        Integer.parseInt(data[2]),
                        Integer.parseInt(data[3]),
                        Double.parseDouble(data[4]),
                        data[5]
                );

                orders.add(order);
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Error reading orders", e
            );
        }

        return orders;
    }

    // WRITE - Save Orders To File
    private void saveOrders(List<Order> orders) {

        try {

            Files.createDirectories(filePath.getParent());

            List<String> lines = new ArrayList<>();

            for (Order order : orders) {

                String line =
                        order.getId() + "|" +
                                order.getCustomerId() + "|" +
                                order.getProductId() + "|" +
                                order.getQuantity() + "|" +
                                order.getTotalPrice() + "|" +
                                order.getStatus().replace("|", " ");

                lines.add(line);
            }

            Files.write(filePath, lines);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Error saving orders", e
            );
        }
    }
}