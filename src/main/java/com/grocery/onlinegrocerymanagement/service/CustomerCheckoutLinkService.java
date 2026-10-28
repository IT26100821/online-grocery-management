
package com.grocery.onlinegrocerymanagement.service;

import com.grocery.onlinegrocerymanagement.model.Customer;
import com.grocery.onlinegrocerymanagement.model.UserAccount;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

@Service
public class CustomerCheckoutLinkService {

    private final RegistrationService registrationService;
    private final CustomerService customerService;

    private final Path linkFile =
            Paths.get("data", "checkout-customer-links.txt");

    public CustomerCheckoutLinkService(
            RegistrationService registrationService,
            CustomerService customerService) {

        this.registrationService = registrationService;
        this.customerService = customerService;
    }

    public synchronized int getCustomerId(String username) {

        UserAccount account =
                registrationService.findByUsername(username);

        if (account == null ||
                !"CUSTOMER".equals(account.getRole())) {

            throw new IllegalArgumentException(
                    "Please log in with a customer account."
            );
        }

        String accountKey = String.valueOf(account.getId());

        try {
            if (Files.exists(linkFile)) {

                for (String line : Files.readAllLines(
                        linkFile, StandardCharsets.UTF_8)) {

                    String[] parts = line.split("\\|", -1);

                    if (parts.length == 2 &&
                            accountKey.equals(parts[0])) {

                        int customerId =
                                Integer.parseInt(parts[1]);

                        Customer existing =
                                customerService.getCustomerById(customerId);

                        if (existing == null) {
                            throw new IllegalStateException(
                                    "Linked customer record is missing."
                            );
                        }

                        return customerId;
                    }
                }
            }

            // Create a Customer Management record
            Customer customer = new Customer();

            customer.setFullName(account.getFullName());
            customer.setEmail(account.getEmail());
            customer.setPhone(account.getPhone());

            customerService.addCustomer(customer);

            int customerId = customer.getId();

            if (customerId <= 0) {
                throw new IllegalStateException(
                        "Unable to create customer record."
                );
            }

            Files.createDirectories(linkFile.getParent());

            Files.writeString(
                    linkFile,
                    accountKey + "|" + customerId
                            + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

            return customerId;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to save customer link.", e
            );
        }
    }
}
