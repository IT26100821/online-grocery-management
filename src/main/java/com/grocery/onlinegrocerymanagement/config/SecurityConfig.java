
package com.grocery.onlinegrocerymanagement.config;

import com.grocery.onlinegrocerymanagement.model.UserAccount;
import com.grocery.onlinegrocerymanagement.service.RegistrationService;

import jakarta.servlet.DispatcherType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth

                // Allow Spring MVC view rendering
                .dispatcherTypeMatchers(
                        DispatcherType.FORWARD,
                        DispatcherType.ERROR
                ).permitAll()

                // Public Pages
                .requestMatchers(
                        "/",
                        "/admin/login",
                        "/customer/login",
                        "/customer/register",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico"
                ).permitAll()

                // NEW: Allow public viewing of product photos
                // Only GET requests are allowed
                .requestMatchers(
                        HttpMethod.GET,
                        "/product-images/**"
                ).permitAll()

                // Super Admin Dashboard
                .requestMatchers("/dashboard")
                .hasRole("SUPER_ADMIN")

                // Customer Management
                .requestMatchers(
                        "/customers",
                        "/customers/**"
                )
                .hasAnyRole(
                        "SUPER_ADMIN",
                        "CUSTOMER_MANAGER"
                )

                // Product Management
                .requestMatchers(
                        "/products",
                        "/products/**"
                )
                .hasAnyRole(
                        "SUPER_ADMIN",
                        "PRODUCT_MANAGER"
                )

                // Category and Inventory Management
                .requestMatchers(
                        "/categories",
                        "/categories/**",
                        "/inventory",
                        "/inventory/**"
                )
                .hasAnyRole(
                        "SUPER_ADMIN",
                        "INVENTORY_MANAGER"
                )

                // Order Management
                .requestMatchers(
                        "/orders",
                        "/orders/**"
                )
                .hasAnyRole(
                        "SUPER_ADMIN",
                        "ORDER_MANAGER"
                )

                // Payment Management
                .requestMatchers(
                        "/payments",
                        "/payments/**"
                )
                .hasAnyRole(
                        "SUPER_ADMIN",
                        "PAYMENT_MANAGER"
                )

                // Delivery Management
                .requestMatchers(
                        "/deliveries",
                        "/deliveries/**"
                )
                .hasAnyRole(
                        "SUPER_ADMIN",
                        "DELIVERY_MANAGER"
                )

                // Customer Dashboard
                .requestMatchers("/customer/dashboard")
                .hasRole("CUSTOMER")

                // Customer Checkout
                .requestMatchers("/checkout", "/checkout/**")
                .hasRole("CUSTOMER")

                // Block everything else
                .anyRequest().denyAll()
        );

        // LOGIN AUTHENTICATION
        http.formLogin(form -> form

                .loginPage("/customer/login")
                .loginProcessingUrl("/login")

                // Redirect user based on role
                .successHandler((request, response,
                                 authentication) -> {

                    String target = "/";

                    var authorities =
                            authentication.getAuthorities();

                    // Super Admin
                    if (authorities.stream().anyMatch(a ->
                            a.getAuthority().equals(
                                    "ROLE_SUPER_ADMIN"))) {

                        target = "/dashboard";

                        // Customer Manager
                    } else if (authorities.stream().anyMatch(a ->
                            a.getAuthority().equals(
                                    "ROLE_CUSTOMER_MANAGER"))) {

                        target = "/customers";

                        // Product Manager
                    } else if (authorities.stream().anyMatch(a ->
                            a.getAuthority().equals(
                                    "ROLE_PRODUCT_MANAGER"))) {

                        target = "/products";

                        // Inventory Manager
                    } else if (authorities.stream().anyMatch(a ->
                            a.getAuthority().equals(
                                    "ROLE_INVENTORY_MANAGER"))) {

                        target = "/";

                        // Order Manager
                    } else if (authorities.stream().anyMatch(a ->
                            a.getAuthority().equals(
                                    "ROLE_ORDER_MANAGER"))) {

                        target = "/orders";

                        // Payment Manager
                    } else if (authorities.stream().anyMatch(a ->
                            a.getAuthority().equals(
                                    "ROLE_PAYMENT_MANAGER"))) {

                        target = "/payments";

                        // Delivery Manager
                    } else if (authorities.stream().anyMatch(a ->
                            a.getAuthority().equals(
                                    "ROLE_DELIVERY_MANAGER"))) {

                        target = "/deliveries";

                        // Customer
                    } else if (authorities.stream().anyMatch(a ->
                            a.getAuthority().equals(
                                    "ROLE_CUSTOMER"))) {

                        target = "/";
                    }

                    response.sendRedirect(
                            request.getContextPath() + target
                    );
                })

                // Login failure handling
                .failureHandler((request, response,
                                 exception) -> {

                    boolean adminPortal =
                            "admin".equals(
                                    request.getParameter("portal")
                            );

                    String target = adminPortal
                            ? "/admin/login?error"
                            : "/customer/login?error";

                    response.sendRedirect(
                            request.getContextPath() + target
                    );
                })

                .permitAll()
        );

        // LOGOUT
        http.logout(logout -> logout

                .logoutUrl("/logout")

                .logoutSuccessUrl(
                        "/customer/login?logout"
                )

                .permitAll()
        );

        return http.build();
    }

    // PASSWORD HASHING
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // USER AUTHENTICATION
    @Bean
    public UserDetailsService userDetailsService(
            RegistrationService registrationService,
            PasswordEncoder passwordEncoder) {

        // Super Admin credentials
        String adminUsername =
                System.getenv(
                        "GREENMART_ADMIN_USERNAME"
                );

        String adminPassword =
                System.getenv(
                        "GREENMART_ADMIN_PASSWORD"
                );

        String adminHash =
                adminPassword != null &&
                        !adminPassword.isBlank()
                        ? passwordEncoder.encode(adminPassword)
                        : null;

        return username -> {

            // Super Admin Login
            if (adminUsername != null
                    && !adminUsername.isBlank()
                    && adminHash != null
                    && adminUsername.equalsIgnoreCase(username)) {

                return User.withUsername(adminUsername)
                        .password(adminHash)
                        .roles("SUPER_ADMIN")
                        .build();
            }

            // Registered Customer Login
            UserAccount account =
                    registrationService.findByUsername(username);

            if (account == null
                    || !"CUSTOMER".equals(account.getRole())) {

                throw new UsernameNotFoundException(
                        "Account not found"
                );
            }

            return User.withUsername(
                            account.getUsername()
                    )
                    .password(
                            account.getPasswordHash()
                    )
                    .roles("CUSTOMER")
                    .build();
        };
    }
}
