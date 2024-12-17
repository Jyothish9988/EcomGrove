package com.example.ecomegrove;

import android.net.Uri;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView userNameTextView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private ViewPager2 viewPager;
    private SliderAdapter sliderAdapter;
    private List<SliderItem> sliderItems;
    private Handler sliderHandler;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        // Initialize Firebase, RecyclerView, and other views
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        userNameTextView = findViewById(R.id.username);
        viewPager = findViewById(R.id.view_pager);
        sliderHandler = new Handler(Looper.getMainLooper());

        sliderItems = new ArrayList<>();
        sliderAdapter = new SliderAdapter(this, sliderItems);
        viewPager.setAdapter(sliderAdapter);

        ImageView imageViewAccount = findViewById(R.id.imageViewAccount);
        imageViewAccount.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        View cartView = findViewById(R.id.cart_view);
        cartView.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        });

        View orders = findViewById(R.id.Orders);
        orders.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, OrderHistoryActivity.class);
            startActivity(intent);
        });


        // Handle Support Icon Click
        ImageView supportIcon = findViewById(R.id.support);
        supportIcon.setOnClickListener(v -> showSupportDetailsDialog());

        ImageView logoutImageView = findViewById(R.id.logout);
        logoutImageView.setOnClickListener(v -> logout());


        // Load initial data
        loadProductsFromFirebase();
        loadSliderData();
        setUserDetails();

        // Implement search functionality
        EditText searchBar = findViewById(R.id.editTextText3);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });
    }

    private void setUserDetails() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userNameTextView.setText("Welcome, Jyothish");
        } else {
            userNameTextView.setText("Welcome, Guest");
        }
    }

    private void loadProductsFromFirebase() {
        db.collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                productList.clear();
                for (DocumentSnapshot document : task.getResult()) {
                    Product product = document.toObject(Product.class);
                    productList.add(product);
                }
                adapter.notifyDataSetChanged();
            } else {
                userNameTextView.setText("Error fetching products.");
            }
        });
    }

    private void loadSliderData() {
        db.collection("sliderImages").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                sliderItems.clear();
                for (DocumentSnapshot document : task.getResult()) {
                    String imageUrl = document.getString("imageUrl");
                    String text = document.getString("text");
                    if (imageUrl != null && text != null) {
                        sliderItems.add(new SliderItem(imageUrl, text));
                    }
                }
                sliderAdapter.notifyDataSetChanged();
                if (!sliderItems.isEmpty()) {
                    startAutoSlide();
                }
            } else {
                userNameTextView.setText("Error fetching slider data.");
            }
        });
    }

    private void startAutoSlide() {
        sliderHandler.postDelayed(() -> {
            int currentItem = viewPager.getCurrentItem();
            int nextItem = (currentItem + 1) % sliderItems.size();
            viewPager.setCurrentItem(nextItem, true);
            sliderHandler.postDelayed(this::startAutoSlide, 3000);
        }, 3000);
    }

    public void filterProducts(String query) {
        if (query == null) query = ""; // Ensure query is not null
        String lowerCaseQuery = query.toLowerCase();

        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            String productName = product.getName();
            if (productName != null && productName.toLowerCase().contains(lowerCaseQuery)) {
                filteredList.add(product);
            }
        }

        adapter.updateProductList(filteredList);
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sliderHandler.removeCallbacksAndMessages(null);
    }


    private void showSupportDetailsDialog() {
        String email = "support@example.com";
        String phone = "+1 234 567 890";
        String website = "https://www.ecomgrove.com";

        new AlertDialog.Builder(this)
                .setTitle("Contact Support")
                .setMessage("Email: " + email + "\nPhone: " + phone + "\nWebsite: " + website)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Visit Website", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                    startActivity(intent);
                })
                .show();
    }

}
