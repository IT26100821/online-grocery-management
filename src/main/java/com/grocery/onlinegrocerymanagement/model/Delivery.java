
package com.grocery.onlinegrocerymanagement.model;

public class Delivery {

    // Attributes
    private int id;
    private int orderId;
    private String deliveryAddress;
    private String deliveryDate;
    private String deliveryStatus;

    // Default Constructor
    public Delivery() {
    }

    // Parameterized Constructor
    public Delivery(int id, int orderId,
                    String deliveryAddress,
                    String deliveryDate,
                    String deliveryStatus) {

        this.id = id;
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
        this.deliveryDate = deliveryDate;
        this.deliveryStatus = deliveryStatus;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
