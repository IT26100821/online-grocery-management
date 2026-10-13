package com.grocery.onlinegrocerymanagement.controller;

import com.grocery.onlinegrocerymanagement.model.Payment;
import com.grocery.onlinegrocerymanagement.service.PaymentService;
import com.grocery.onlinegrocerymanagement.service.OrderService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    // Constructor
    public PaymentController(PaymentService paymentService,
                             OrderService orderService) {

        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    // READ - View All Payments
    @GetMapping
    public String viewPayments(Model model) {

        model.addAttribute("payments",
                paymentService.getAllPayments());

        model.addAttribute("orders",
                orderService.getAllOrders());

        return "payments";
    }

    // CREATE - Add Payment
    @PostMapping("/add")
    public String addPayment(
            @ModelAttribute Payment payment,
            RedirectAttributes redirectAttributes) {

        try {

            paymentService.addPayment(payment);

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage()
            );
        }

        return "redirect:/payments";
    }

    // Open Edit Payment Page
    @GetMapping("/edit/{id}")
    public String editPayment(@PathVariable int id,
                              Model model) {

        Payment payment = paymentService.getPaymentById(id);

        if (payment == null) {
            return "redirect:/payments";
        }

        model.addAttribute("payment", payment);

        model.addAttribute("orders",
                orderService.getAllOrders());

        return "edit-payment";
    }

    // UPDATE - Update Payment
    @PostMapping("/update/{id}")
    public String updatePayment(
            @PathVariable int id,
            @ModelAttribute Payment payment,
            RedirectAttributes redirectAttributes) {

        try {

            paymentService.updatePayment(id, payment);

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage()
            );

            return "redirect:/payments/edit/" + id;
        }

        return "redirect:/payments";
    }

    // DELETE - Delete Payment
    @PostMapping("/delete/{id}")
    public String deletePayment(@PathVariable int id) {

        paymentService.deletePayment(id);

        return "redirect:/payments";
    }
}
