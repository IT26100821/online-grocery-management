
package com.grocery.onlinegrocerymanagement.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginPageController {

    // Admin Login Page
    @GetMapping("/admin/login")
    public String adminLogin(HttpServletRequest request) {

        initializeCsrfToken(request);

        return "admin-login";
    }

    // Customer Login Page
    @GetMapping("/customer/login")
    public String customerLogin(HttpServletRequest request) {

        initializeCsrfToken(request);

        return "customer-login";
    }

    // Customer Registration Page
    @GetMapping("/customer/register")
    public String customerRegister(HttpServletRequest request) {

        initializeCsrfToken(request);

        return "customer-register";
    }

    // Load CSRF Token before HTML rendering
    private void initializeCsrfToken(HttpServletRequest request) {

        CsrfToken csrfToken = (CsrfToken)
                request.getAttribute(CsrfToken.class.getName());

        if (csrfToken != null) {
            csrfToken.getToken();
        }
    }
}
