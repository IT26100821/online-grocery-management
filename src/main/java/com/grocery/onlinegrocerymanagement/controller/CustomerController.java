package com.grocery.onlinegrocerymanagement.controller;

import com.grocery.onlinegrocerymanagement.model.Customer;
import com.grocery.onlinegrocerymanagement.service.CustomerService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // READ - View All Customers
    @GetMapping
    public String viewCustomers(Model model) {

        model.addAttribute(
                "customers",
                customerService.getAllCustomers()
        );

        return "customers";
    }

    // CREATE - Add New Customer
    @PostMapping("/add")
    public String addCustomer(@ModelAttribute Customer customer) {

        if (!isValid(customer)) {
            return "redirect:/customers";
        }

        customerService.addCustomer(customer);

        return "redirect:/customers";
    }

    // Open Edit Customer Page
    @GetMapping("/edit/{id}")
    public String editCustomer(
            @PathVariable int id,
            Model model) {

        Customer customer = customerService.getCustomerById(id);

        if (customer == null) {
            return "redirect:/customers";
        }

        model.addAttribute("customer", customer);

        return "edit-customer";
    }

    // UPDATE - Update Customer
    @PostMapping("/update/{id}")
    public String updateCustomer(
            @PathVariable int id,
            @ModelAttribute Customer customer) {

        if (customerService.getCustomerById(id) == null) {
            return "redirect:/customers";
        }

        if (!isValid(customer)) {
            return "redirect:/customers/edit/" + id;
        }

        customerService.updateCustomer(id, customer);

        return "redirect:/customers";
    }

    // DELETE - Delete Customer
    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable int id) {

        customerService.deleteCustomer(id);

        return "redirect:/customers";
    }

    // Basic Customer Validation
    private boolean isValid(Customer customer) {

        return customer.getFullName() != null
                && !customer.getFullName().isBlank()
                && customer.getEmail() != null
                && customer.getEmail().contains("@")
                && customer.getPhone() != null
                && !customer.getPhone().isBlank();
    }
}

