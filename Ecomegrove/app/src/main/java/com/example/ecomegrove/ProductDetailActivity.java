package com.example.ecomegrove;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView productName, productPrice, productDescription;
    private RatingBar productRating;
    private ImageView productImage;
    private Button addToCartButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);
        FirebaseApp.initializeApp(this);

        // Initialize Firestore, FirebaseAuth, and Views
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        productName = findViewById(R.id.productNameTextView);
        productPrice = findViewById(R.id.productPriceTextView);
        productDescription = findViewById(R.id.productDescriptionTextView);
        productImage = findViewById(R.id.productImageView);
        productRating = findViewById(R.id.productRating);
        addToCartButton = findViewById(R.id.addToCartButton);

        // Get the product serial from the Intent
        String productSerial = getIntent().getStringExtra("PRODUCT_SERIAL");

        // Fetch product details from Firestore using the product serial
        if (productSerial != null) {
            loadProductDetails(productSerial);
        }

        // Add to Cart button functionality
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    addToCart(productSerial);
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Please log in to add items to the cart.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadProductDetails(@NonNull String productSerialFromIntent) {
        db.collection("products")
                .whereEqualTo("serial", productSerialFromIntent) // Query using the serial
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Get the first document (assuming serials are unique)
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                        // Parse product details from Firestore
                        String name = documentSnapshot.getString("name");
                        String price = documentSnapshot.getString("price");
                        String description = documentSnapshot.getString("description");
                        String imageUrl = documentSnapshot.getString("imageUrl");
                        String ratingString = documentSnapshot.getString("rating");

                        // Update UI with product details
                        productName.setText(name);
                        productPrice.setText(price);
                        productDescription.setText(description);

                        // Convert rating to float and set it to the RatingBar
                        if (ratingString != null) {
                            try {
                                float rating = Float.parseFloat(ratingString); // Parse the rating to float
                                productRating.setRating(rating); // Set rating to RatingBar
                            } catch (NumberFormatException e) {
                                productRating.setRating(0); // Default rating if parsing fails
                            }
                        }

                        // Load image using Glide
                        Glide.with(this).load(imageUrl).into(productImage);
                    } else {
                        productName.setText("Product not found");
                    }
                })
                .addOnFailureListener(e -> productName.setText("Error loading product details"));
    }

    private void addToCart(String productSerial) {
        // Get current user ID
        String userId = mAuth.getCurrentUser().getUid();

        // Create a Map for cart item data
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("productSerial", productSerial);
        cartItem.put("productName", productName.getText().toString());
        cartItem.put("productPrice", productPrice.getText().toString());
        cartItem.put("productDescription", productDescription.getText().toString());
        cartItem.put("rating", productRating.getRating());
        cartItem.put("quantity", 1); // Assuming 1 quantity for now

        // Add to Firestore under the user's cart
        db.collection("carts")
                .document(userId)  // User-specific cart
                .collection("items")
                .document(productSerial)  // Product-specific
                .set(cartItem)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProductDetailActivity.this, "Added to cart!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProductDetailActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                });
    }
}
