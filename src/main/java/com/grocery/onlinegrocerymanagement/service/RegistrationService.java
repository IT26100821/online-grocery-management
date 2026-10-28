
package com.grocery.onlinegrocerymanagement.service;

import com.grocery.onlinegrocerymanagement.model.UserAccount;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegistrationService {

    private final Path filePath =
            Paths.get("data", "user-accounts.txt");

    private final PasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    // CREATE - Register Customer
    public synchronized void registerCustomer(
            String fullName,
            String username,
            String email,
            String phone,
            String password,
            String confirmPassword) {

        // Remove unwanted spaces
        fullName = clean(fullName);
        username = clean(username);
        email = clean(email);
        phone = clean(phone);

        // Validate full name
        if (fullName.isBlank() || fullName.length() > 100
                || containsInvalidCharacters(fullName)) {

            throw new IllegalArgumentException(
                    "Invalid full name"
            );
        }

        // Validate username
        if (!username.matches("[A-Za-z0-9._-]{3,40}")) {

            throw new IllegalArgumentException(
                    "Username must be 3-40 characters"
            );
        }

        // Validate email
        if (email.length() > 150
                || !email.matches(
                "^[^\\s@|]+@[^\\s@|]+\\.[^\\s@|]+$")) {

            throw new IllegalArgumentException(
                    "Invalid email address"
            );
        }

        // Validate phone
        if (!phone.matches("[+0-9 ()-]{7,20}")) {

            throw new IllegalArgumentException(
                    "Invalid phone number"
            );
        }

        // Validate password
        if (password == null
                || password.length() < 8
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {

            throw new IllegalArgumentException(
                    "Password must be at least 8 characters "
                            + "and at most 72 UTF-8 bytes"
            );
        }

        if (!password.equals(confirmPassword)) {

            throw new IllegalArgumentException(
                    "Passwords do not match"
            );
        }

        // Load existing accounts
        List<UserAccount> accounts = loadAccounts();

        // Check duplicate username or email
        for (UserAccount account : accounts) {

            if (account.getUsername()
                    .equalsIgnoreCase(username)) {

                throw new IllegalArgumentException(
                        "Username already exists"
                );
            }

            if (account.getEmail()
                    .equalsIgnoreCase(email)) {

                throw new IllegalArgumentException(
                        "Email already registered"
                );
            }
        }

        // Generate new account ID
        int newId = 1;

        for (UserAccount account : accounts) {

            if (account.getId() >= newId) {
                newId = account.getId() + 1;
            }
        }

        // Securely hash password
        String passwordHash =
                passwordEncoder.encode(password);

        // Create new customer account
        UserAccount newAccount = new UserAccount(
                newId,
                fullName,
                username,
                email,
                phone,
                passwordHash,
                "CUSTOMER"
        );

        // Save account
        accounts.add(newAccount);

        saveAccounts(accounts);
    }

    // Find account for future login logic
    public synchronized UserAccount findByUsername(
            String username) {

        if (username == null) {
            return null;
        }

        for (UserAccount account : loadAccounts()) {

            if (account.getUsername()
                    .equalsIgnoreCase(username)) {

                return account;
            }
        }

        return null;
    }

    // Read accounts from file
    private List<UserAccount> loadAccounts() {

        List<UserAccount> accounts = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return accounts;
        }

        try {

            List<String> lines = Files.readAllLines(
                    filePath, StandardCharsets.UTF_8);

            for (String line : lines) {

                if (line.isBlank()) {
                    continue;
                }

                String[] data = line.split("\\|", -1);

                if (data.length != 7) {
                    throw new IllegalStateException(
                            "Invalid account data"
                    );
                }

                UserAccount account = new UserAccount(
                        Integer.parseInt(data[0]),
                        data[1],
                        data[2],
                        data[3],
                        data[4],
                        data[5],
                        data[6]
                );

                accounts.add(account);
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error reading user accounts", e
            );
        }

        return accounts;
    }

    // Save accounts to file
    private void saveAccounts(List<UserAccount> accounts) {

        try {

            Files.createDirectories(filePath.getParent());

            List<String> lines = new ArrayList<>();

            for (UserAccount account : accounts) {

                String line =
                        account.getId() + "|" +
                                account.getFullName() + "|" +
                                account.getUsername() + "|" +
                                account.getEmail() + "|" +
                                account.getPhone() + "|" +
                                account.getPasswordHash() + "|" +
                                account.getRole();

                lines.add(line);
            }

            Files.write(
                    filePath,
                    lines,
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error saving user accounts", e
            );
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean containsInvalidCharacters(String value) {
        return value.contains("|")
                || value.contains("\n")
                || value.contains("\r");
    }
}
