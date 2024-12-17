package com.example.ecomegrove;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItemList;
    private FirebaseFirestore db;
    private String userId;

    public CartAdapter(List<CartItem> cartItemList, FirebaseFirestore db, String userId) {
        this.cartItemList = cartItemList;
        this.db = db;
        this.userId = userId;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);

        // Set cart item details
        holder.productName.setText(cartItem.getProductName());
        holder.productPrice.setText(cartItem.getProductPrice());
        holder.productDescription.setText(cartItem.getProductDescription());
        holder.quantity.setText(String.valueOf(cartItem.getQuantity()));

        // Set rating
        holder.rating.setRating(cartItem.getRating());



        // Set delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            String productName = cartItem.getProductName();
            db.collection("carts")
                    .document(userId)
                    .collection("items")
                    .document(productName)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        cartItemList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(holder.itemView.getContext(), "Item removed from cart.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(holder.itemView.getContext(), "Error deleting item.", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        TextView productName, productPrice, productDescription, quantity;
        RatingBar rating;
        ImageView productImage;
        Button deleteButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productDescription = itemView.findViewById(R.id.productDescription);
            rating = itemView.findViewById(R.id.ratingBar); // Fixed RatingBar
            quantity = itemView.findViewById(R.id.quantity);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
