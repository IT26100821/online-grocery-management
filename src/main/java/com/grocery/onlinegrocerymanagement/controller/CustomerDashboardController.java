
package com.grocery.onlinegrocerymanagement.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class CustomerDashboardController {

    @GetMapping("/customer/dashboard")
    public String customerDashboard(
            Principal principal,
            Model model,
            HttpServletRequest request) {

        // Get currently logged-in username
        model.addAttribute(
                "username",
                principal.getName()
        );

        // Initialize CSRF before rendering Logout form
        CsrfToken csrfToken = (CsrfToken)
                request.getAttribute(
                        CsrfToken.class.getName()
                );

        if (csrfToken != null) {
            csrfToken.getToken();
        }

        return "customer-dashboard";
    }
}
