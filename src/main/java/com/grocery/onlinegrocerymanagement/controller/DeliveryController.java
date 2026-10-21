
package com.grocery.onlinegrocerymanagement.controller;

import com.grocery.onlinegrocerymanagement.model.Delivery;
import com.grocery.onlinegrocerymanagement.service.DeliveryService;
import com.grocery.onlinegrocerymanagement.service.OrderService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final OrderService orderService;

    // Constructor
    public DeliveryController(DeliveryService deliveryService,
                              OrderService orderService) {

        this.deliveryService = deliveryService;
        this.orderService = orderService;
    }

    // READ - View All Deliveries
    @GetMapping
    public String viewDeliveries(Model model) {

        model.addAttribute("deliveries",
                deliveryService.getAllDeliveries());

        model.addAttribute("orders",
                orderService.getAllOrders());

        return "deliveries";
    }

    // CREATE - Add New Delivery
    @PostMapping("/add")
    public String addDelivery(
            @ModelAttribute Delivery delivery,
            RedirectAttributes redirectAttributes) {

        try {

            deliveryService.addDelivery(delivery);

            redirectAttributes.addFlashAttribute(
                    "success", "Delivery added successfully"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage()
            );
        }

        return "redirect:/deliveries";
    }

    // Open Edit Delivery Page
    @GetMapping("/edit/{id}")
    public String editDelivery(
            @PathVariable int id,
            Model model) {

        Delivery delivery =
                deliveryService.getDeliveryById(id);

        if (delivery == null) {
            return "redirect:/deliveries";
        }

        model.addAttribute("delivery", delivery);

        model.addAttribute("orders",
                orderService.getAllOrders());

        return "edit-delivery";
    }

    // UPDATE - Update Delivery
    @PostMapping("/update/{id}")
    public String updateDelivery(
            @PathVariable int id,
            @ModelAttribute Delivery delivery,
            RedirectAttributes redirectAttributes) {

        try {

            deliveryService.updateDelivery(id, delivery);

            redirectAttributes.addFlashAttribute(
                    "success", "Delivery updated successfully"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage()
            );

            return "redirect:/deliveries/edit/" + id;
        }

        return "redirect:/deliveries";
    }

    // DELETE - Delete Delivery
    @PostMapping("/delete/{id}")
    public String deleteDelivery(
            @PathVariable int id,
            RedirectAttributes redirectAttributes) {

        deliveryService.deleteDelivery(id);

        redirectAttributes.addFlashAttribute(
                "success", "Delivery deleted successfully"
        );

        return "redirect:/deliveries";
    }
}
