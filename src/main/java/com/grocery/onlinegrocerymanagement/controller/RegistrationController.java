
package com.grocery.onlinegrocerymanagement.controller;

import com.grocery.onlinegrocerymanagement.service.RegistrationService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    private final RegistrationService registrationService;

    // Constructor
    public RegistrationController(
            RegistrationService registrationService) {

        this.registrationService = registrationService;
    }

    // REGISTER NEW CUSTOMER
    @PostMapping("/customer/register")
    public String registerCustomer(

            @RequestParam String fullName,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String password,
            @RequestParam String confirmPassword,

            RedirectAttributes redirectAttributes) {

        try {

            // Save customer account
            registrationService.registerCustomer(
                    fullName,
                    username,
                    email,
                    phone,
                    password,
                    confirmPassword
            );

            // Registration successful
            return "redirect:/customer/login?registered";

        } catch (IllegalArgumentException e) {

            // Display validation error
            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );

            return "redirect:/customer/register";
        }
    }
}
