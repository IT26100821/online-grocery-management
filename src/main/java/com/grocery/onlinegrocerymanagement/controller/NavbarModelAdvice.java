
package com.grocery.onlinegrocerymanagement.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NavbarModelAdvice {

    @ModelAttribute
    public void addNavbarDetails(Model model) {

        Authentication auth =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        boolean loggedIn =
                auth != null &&
                        auth.isAuthenticated() &&
                        !(auth instanceof AnonymousAuthenticationToken);

        boolean superAdmin = loggedIn &&
                auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority()
                                .equals("ROLE_SUPER_ADMIN"));

        model.addAttribute("gmLoggedIn", loggedIn);
        model.addAttribute("gmSuperAdmin", superAdmin);

        model.addAttribute(
                "gmUsername",
                loggedIn ? auth.getName() : ""
        );
    }
}
