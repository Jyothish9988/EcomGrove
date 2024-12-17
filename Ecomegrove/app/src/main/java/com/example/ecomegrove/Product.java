package com.example.ecomegrove;

public class Product {
    private String name;
    private String price;
    private String imageUrl;
    private String serial;
    private int quantity;
    private String rating;

    // No-argument constructor (required for Firestore)
    public Product() {
    }

    // Parameterized constructor
    public Product(String name, String price, String imageUrl, String serial, String rating) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.serial = serial;
        this.rating = rating;
        this.quantity = 1; // Default quantity is 1 when added to the cart
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public int getQuantity() {
        return quantity;
    }

    public void incrementQuantity() {
        this.quantity++;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Get rating as a double (if it's stored as a String)
    public double getRatingAsDouble() {
        try {
            return Double.parseDouble(rating);
        } catch (NumberFormatException e) {
            return 0.0; // Default value if parsing fails
        }
    }

    // Set rating (accepting a String to match Firestore data type)
    public void setRating(String rating) {
        this.rating = rating;
    }
}
