package com.grocery.onlinegrocerymanagement.controller;

import com.grocery.onlinegrocerymanagement.model.Order;
import com.grocery.onlinegrocerymanagement.service.OrderService;
import com.grocery.onlinegrocerymanagement.service.ProductService;
import com.grocery.onlinegrocerymanagement.service.CustomerService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final ProductService productService;
    private final CustomerService customerService;

    // Constructor
    public OrderController(OrderService orderService,
                           ProductService productService,
                           CustomerService customerService) {

        this.orderService = orderService;
        this.productService = productService;
        this.customerService = customerService;
    }

    // READ - View All Orders
    @GetMapping
    public String viewOrders(Model model) {

        model.addAttribute("orders",
                orderService.getAllOrders());

        model.addAttribute("customers",
                customerService.getAllCustomers());

        model.addAttribute("products",
                productService.getAllProducts());

        return "orders";
    }

    // CREATE - Add New Order
    @PostMapping("/add")
    public String addOrder(
            @ModelAttribute Order order,
            RedirectAttributes redirectAttributes) {

        if (!validDetails(order)) {
            redirectAttributes.addFlashAttribute(
                    "error", "Invalid customer, product or quantity");
            return "redirect:/orders";
        }

        orderService.addOrder(order);

        return "redirect:/orders";
    }

    // Open Edit Order Page
    @GetMapping("/edit/{id}")
    public String editOrder(@PathVariable int id, Model model) {

        Order order = orderService.getOrderById(id);

        if (order == null) {
            return "redirect:/orders";
        }

        model.addAttribute("order", order);

        model.addAttribute("customers",
                customerService.getAllCustomers());

        model.addAttribute("products",
                productService.getAllProducts());

        return "edit-order";
    }

    // UPDATE - Update Order
    @PostMapping("/update/{id}")
    public String updateOrder(
            @PathVariable int id,
            @ModelAttribute Order order,
            RedirectAttributes redirectAttributes) {

        if (orderService.getOrderById(id) == null) {
            return "redirect:/orders";
        }

        if (!validDetails(order) || !validStatus(order.getStatus())) {
            redirectAttributes.addFlashAttribute(
                    "error", "Invalid order details");
            return "redirect:/orders/edit/" + id;
        }

        orderService.updateOrder(id, order);

        return "redirect:/orders";
    }

    // DELETE - Delete Order
    @PostMapping("/delete/{id}")
    public String deleteOrder(@PathVariable int id) {

        orderService.deleteOrder(id);

        return "redirect:/orders";
    }

    // Validate Order Details
    private boolean validDetails(Order order) {

        return order.getQuantity() > 0
                && customerService.getCustomerById(
                order.getCustomerId()) != null
                && productService.getProductById(
                order.getProductId()) != null;
    }

    // Validate Order Status
    private boolean validStatus(String status) {

        return "Pending".equals(status)
                || "Confirmed".equals(status)
                || "Delivered".equals(status)
                || "Cancelled".equals(status);
    }
}