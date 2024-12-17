package com.example.ecomegrove;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;  // <-- Add this line
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;  // <-- Add this import
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.firestore.DocumentSnapshot;

public class CheckoutActivity extends AppCompatActivity {

    private EditText addressInput;
    private Button placeOrderButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView totalAmountTextView;  // Reference to Total Amount TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_activity);

        addressInput = findViewById(R.id.addressInput);
        placeOrderButton = findViewById(R.id.placeOrderButton);
        totalAmountTextView = findViewById(R.id.totalAmount);  // Initialize the TextView
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        placeOrderButton.setOnClickListener(v -> {
            String address = addressInput.getText().toString().trim();
            if (address.isEmpty()) {
                Toast.makeText(this, "Please enter your address.", Toast.LENGTH_SHORT).show();
            } else {
                // Show the address in an AlertDialog
                showAddressConfirmationDialog(address);
            }
        });

        // Fetch the cart items and calculate the total amount
        calculateTotalAmount();
    }

    private void calculateTotalAmount() {
        String userId = mAuth.getCurrentUser().getUid();

        // Reference to the user's cart
        db.collection("carts")
                .document(userId)
                .collection("items")  // Assuming items are stored under a sub-collection called "items"
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalAmount = 0.0;
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String priceString = documentSnapshot.getString("productPrice");  // Get the product price
                        int quantity = documentSnapshot.getLong("quantity").intValue();  // Get the quantity

                        if (priceString != null) {
                            // Convert price string to double, assuming it's in the format "₹48,990"
                            double price = Double.parseDouble(priceString.replace("₹", "").replace(",", ""));
                            totalAmount += price * quantity;  // Multiply price by quantity
                        }
                    }

                    // Update the total amount TextView
                    totalAmountTextView.setText("Total Amount: ₹" + totalAmount);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching cart items.", Toast.LENGTH_SHORT).show();
                });
    }

    private void showAddressConfirmationDialog(String address) {
        // Create an AlertDialog to show the address before placing the order
        new AlertDialog.Builder(this)
                .setTitle("Confirm Address")
                .setMessage("Your address: " + address)
                .setPositiveButton("Confirm", (dialog, which) -> placeOrder(address))
                .setNegativeButton("Edit", null)
                .show();
    }

    private void placeOrder(String address) {
        String userId = mAuth.getCurrentUser().getUid();

        // Get current date and time
        String currentDate = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());

        // Fetch cart items
        db.collection("carts")
                .document(userId)
                .collection("items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Create a list to store product names and prices
                    Map<String, Object> order = new HashMap<>();
                    order.put("address", address);
                    order.put("paymentMethod", "Cash on Delivery");
                    order.put("date", currentDate);  // Add the date of order

                    // Store product details as a list of maps
                    ArrayList<Map<String, Object>> productList = new ArrayList<>();  // This line is now valid
                    double totalAmount = 0.0;

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String productName = documentSnapshot.getString("productName");
                        String priceString = documentSnapshot.getString("productPrice");
                        int quantity = documentSnapshot.getLong("quantity").intValue();

                        if (priceString != null && productName != null) {
                            // Convert price string to double, assuming it's in the format "₹48,990"
                            double price = Double.parseDouble(priceString.replace("₹", "").replace(",", ""));
                            totalAmount += price * quantity;

                            // Create a map for each product
                            Map<String, Object> product = new HashMap<>();
                            product.put("productName", productName);
                            product.put("price", price);
                            product.put("quantity", quantity);

                            // Add product to the list
                            productList.add(product);
                        }
                    }

                    // Add product details and total amount to the order
                    order.put("products", productList);
                    order.put("totalAmount", totalAmount);

                    // Add order to the Firestore collection
                    db.collection("orders")
                            .document(userId)
                            .set(order)  // Save the order as a map
                            .addOnSuccessListener(unused -> {
                                // Clear the user's cart after placing the order
                                clearUserCart(userId);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error placing order.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching cart items.", Toast.LENGTH_SHORT).show();
                });
    }


    private void clearUserCart(String userId) {
        // Remove all items from the user's cart in Firestore
        db.collection("carts")
                .document(userId)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Order placed successfully! Your cart is now empty.", Toast.LENGTH_SHORT).show();
                    // Navigate to the MainActivity after placing the order
                    navigateToMainActivity();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Order placed, but could not clear your cart.", Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToMainActivity() {
        // Navigate to MainActivity
        Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the checkout activity from the stack
        startActivity(intent);
        finish();  // Close the CheckoutActivity
    }
}
