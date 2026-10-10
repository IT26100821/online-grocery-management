package com.grocery.onlinegrocerymanagement.service;

import com.grocery.onlinegrocerymanagement.model.Customer;
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
public class CustomerService {

    private final Path filePath =
            Paths.get("data", "customers.txt");


    // READ - View All Customers
    public synchronized List<Customer> getAllCustomers() {

        return loadCustomers();
    }


    // CREATE - Add New Customer
    public synchronized void addCustomer(Customer customer) {

        List<Customer> customers = loadCustomers();

        int newId = customers.stream()
                .mapToInt(Customer::getId)
                .max()
                .orElse(0) + 1;

        customer.setId(newId);

        customers.add(customer);

        saveCustomers(customers);
    }


    // READ - Find Customer By ID
    public synchronized Customer getCustomerById(int id) {

        for (Customer customer : loadCustomers()) {

            if (customer.getId() == id) {
                return customer;
            }
        }

        return null;
    }


    // UPDATE - Update Customer Details
    public synchronized void updateCustomer(
            int id, Customer updatedCustomer) {

        List<Customer> customers = loadCustomers();

        for (int i = 0; i < customers.size(); i++) {

            if (customers.get(i).getId() == id) {

                updatedCustomer.setId(id);

                customers.set(i, updatedCustomer);

                saveCustomers(customers);

                return;
            }
        }

        throw new IllegalArgumentException(
                "Customer not found"
        );
    }


    // DELETE - Delete Customer
    public synchronized void deleteCustomer(int id) {

        List<Customer> customers = loadCustomers();

        boolean removed = customers.removeIf(
                customer -> customer.getId() == id
        );

        if (removed) {
            saveCustomers(customers);
        }
    }


    // Load Customers From Text File
    private List<Customer> loadCustomers() {

        List<Customer> customers = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return customers;
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

                if (data.length != 4) {
                    throw new IllegalStateException(
                            "Invalid customer data"
                    );
                }

                Customer customer = new Customer(
                        Integer.parseInt(data[0]),
                        data[1],
                        data[2],
                        data[3]
                );

                customers.add(customer);
            }

        } catch (IOException e) {

            throw new UncheckedIOException(
                    "Error reading customers file", e
            );
        }

        return customers;
    }


    // Save Customers To Text File
    private void saveCustomers(List<Customer> customers) {

        try {

            Files.createDirectories(filePath.getParent());

            List<String> lines = new ArrayList<>();

            for (Customer customer : customers) {

                String line =
                        customer.getId() + "|" +
                                clean(customer.getFullName()) + "|" +
                                clean(customer.getEmail()) + "|" +
                                clean(customer.getPhone());

                lines.add(line);
            }

            Files.write(
                    filePath,
                    lines,
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {

            throw new UncheckedIOException(
                    "Error saving customers file", e
            );
        }
    }


    // Clean Text Before Saving
    private String clean(String value) {

        if (value == null) {
            return "";
        }

        return value.replace("|", " ")
                .replace("\n", " ")
                .replace("\r", " ");
    }
}
