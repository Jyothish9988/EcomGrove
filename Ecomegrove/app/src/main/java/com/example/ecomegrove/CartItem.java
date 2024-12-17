package com.example.ecomegrove;

public class CartItem {
    private String productName;
    private String productPrice;
    private String productDescription;
    private String category;
    private float rating;
    private int quantity;

    public CartItem(String productName, String productPrice, String productDescription,
                    String category, float rating, int quantity) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.productDescription = productDescription;
        this.category = category;
        this.rating = rating;
        this.quantity = quantity;
    }

    // Getters for all fields
    public String getProductName() {
        return productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getCategory() {
        return category;
    }

    public float getRating() {
        return rating;
    }

    public int getQuantity() {
        return quantity;
    }
}
