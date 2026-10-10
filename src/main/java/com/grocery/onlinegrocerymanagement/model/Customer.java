package com.grocery.onlinegrocerymanagement.model;

public class Customer {

    private int id;
    private String fullName;
    private String email;
    private String phone;

    // Default Constructor
    public Customer() {

    }

    // Parameterized Constructor
    public Customer(int id, String fullName,
                    String email, String phone) {

        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    // Get Customer ID
    public int getId() {
        return id;
    }

    // Set Customer ID
    public void setId(int id) {
        this.id = id;
    }

    // Get Customer Name
    public String getFullName() {
        return fullName;
    }

    // Set Customer Name
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Get Customer Email
    public String getEmail() {
        return email;
    }

    // Set Customer Email
    public void setEmail(String email) {
        this.email = email;
    }

    // Get Customer Phone
    public String getPhone() {
        return phone;
    }

    // Set Customer Phone
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
