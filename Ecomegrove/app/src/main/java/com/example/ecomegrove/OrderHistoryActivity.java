package com.example.ecomegrove;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView orderRecyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList);

        orderRecyclerView.setAdapter(orderAdapter);

        // Load orders from Firestore
        FirebaseFirestore.getInstance().collection("orders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                // Extract order data
                                String date = documentSnapshot.getString("date");
                                String address = documentSnapshot.getString("address");
                                String paymentMethod = documentSnapshot.getString("paymentMethod");
                                double totalAmount = documentSnapshot.getDouble("totalAmount");

                                // Extract product list
                                ArrayList<Map<String, Object>> products =
                                        (ArrayList<Map<String, Object>>) documentSnapshot.get("products");

                                ArrayList<Product> productList = new ArrayList<>();
                                if (products != null) {
                                    for (Map<String, Object> productMap : products) {
                                        String name = (String) productMap.get("productName");

                                        // Fixing price retrieval
                                        Object priceObj = productMap.get("price");
                                        String price = "";
                                        if (priceObj instanceof Double) {
                                            price = String.valueOf(priceObj);  // Convert Double to String
                                        } else if (priceObj instanceof String) {
                                            price = (String) priceObj;  // Already a String
                                        }

                                        String imageUrl = (String) productMap.get("imageUrl");
                                        String serial = (String) productMap.get("serial");
                                        int quantity = ((Long) productMap.get("quantity")).intValue();

                                        // Use the existing constructor with String, String, String, String, String
                                        Product product = new Product(name, price, imageUrl, serial, null);
                                        product.setQuantity(quantity);  // Set quantity
                                        productList.add(product);
                                    }
                                }

                                // Create Order object
                                Order order = new Order(date, address, paymentMethod, totalAmount, productList);
                                orderList.add(order);
                            }
                            orderAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(this, "Failed to load orders", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
