package com.example.ecomegrove;

import java.util.ArrayList;
import java.util.List; // Import the List interface

public class Order {
    private String date;
    private String address;
    private String paymentMethod;
    private double totalAmount;
    private ArrayList<Product> products;

    // Constructor
    public Order(String date, String address, String paymentMethod, double totalAmount, ArrayList<Product> products) {
        this.date = date;
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.products = products;
    }

    // Getter for productList
    public List<Product> getProductList() {
        return products; // Correctly returns the ArrayList
    }

    public String getDate() {
        return date;
    }

    public String getAddress() {
        return address;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }
}
