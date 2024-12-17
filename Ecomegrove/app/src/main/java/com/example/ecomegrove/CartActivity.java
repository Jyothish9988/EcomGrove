package com.example.ecomegrove;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private Button buyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_activity);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.cartRecyclerView);
        buyButton = findViewById(R.id.buyButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItemList = new ArrayList<>();

        loadCartItems();

        // Navigate to checkout on button click
        buyButton.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            startActivity(intent);
        });
    }

    private void loadCartItems() {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("carts")
                .document(userId)
                .collection("items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        cartItemList.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Parse the cart item data
                            String productName = documentSnapshot.getString("productName");
                            String productPrice = documentSnapshot.getString("productPrice");
                            String productDescription = documentSnapshot.getString("productDescription");
                            String category = documentSnapshot.getString("category"); // Example of missing field
                            float rating = documentSnapshot.getDouble("rating").floatValue();
                            int quantity = documentSnapshot.getLong("quantity").intValue();
                            // Remove imageUrl field from data
                            // String imageUrl = documentSnapshot.getString("imageUrl");

                            // Create a CartItem object without imageUrl
                            CartItem cartItem = new CartItem(productName, productPrice, productDescription,
                                    category, rating, quantity);
                            cartItemList.add(cartItem);
                        }

                        // Set up the adapter to display the cart items
                        cartAdapter = new CartAdapter(cartItemList, db, userId);
                        recyclerView.setAdapter(cartAdapter);
                    } else {
                        Toast.makeText(CartActivity.this, "Your cart is empty.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CartActivity.this, "Error loading cart items.", Toast.LENGTH_SHORT).show();
                });
    }

}
