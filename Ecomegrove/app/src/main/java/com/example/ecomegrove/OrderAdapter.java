package com.example.ecomegrove;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.dateTextView.setText("Date: " + order.getDate());
        holder.addressTextView.setText("Address: " + order.getAddress());
        holder.paymentMethodTextView.setText("Payment: " + order.getPaymentMethod());
        holder.totalAmountTextView.setText("Total: ₹" + order.getTotalAmount());

        // Concatenate product details
        StringBuilder productsBuilder = new StringBuilder();
        for (Product product : order.getProducts()) {
            productsBuilder.append(product.getName())
                    .append(" - ₹")
                    .append(product.getPrice())
                    .append(" (Qty: ")
                    .append(product.getQuantity())
                    .append(")\n");
        }
        holder.productsTextView.setText("Products:\n" + productsBuilder.toString().trim());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, addressTextView, paymentMethodTextView, totalAmountTextView, productsTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            paymentMethodTextView = itemView.findViewById(R.id.paymentMethodTextView);
            totalAmountTextView = itemView.findViewById(R.id.totalAmountTextView);
            productsTextView = itemView.findViewById(R.id.productsTextView);
        }
    }
}
