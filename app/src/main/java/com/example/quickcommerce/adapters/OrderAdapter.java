package com.example.quickcommerce.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quickcommerce.R;
import com.example.quickcommerce.models.MyOrderDisplay;

import java.util.List;

public class OrderAdapter extends ListAdapter<MyOrderDisplay, OrderAdapter.OrderViewHolder> {

    private OnOrderClickListener onOrderClickListener;

    public interface OnOrderClickListener {
        void onOrderClick(MyOrderDisplay order);
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.onOrderClickListener = listener;
    }

    public OrderAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<MyOrderDisplay> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<MyOrderDisplay>() {
                @Override
                public boolean areItemsTheSame(@NonNull MyOrderDisplay oldItem, @NonNull MyOrderDisplay newItem) {
                    return oldItem.getOrderId().equals(newItem.getOrderId());
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(@NonNull MyOrderDisplay oldItem, @NonNull MyOrderDisplay newItem) {
                    return oldItem.equals(newItem);
                }
            };

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_myorder, parent, false);
        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        MyOrderDisplay order = getItem(position);

        holder.orderDetailsText.setText(order.getTotalAmount());
        holder.orderDate.setText(order.getOrderDate());
        holder.ratingBar.setRating(order.getRating());
        holder.ratingBar.setProgressTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.gold));

        // Handle order status mapping
        String rawStatus = order.getOrderStatus();
        if (rawStatus == null) rawStatus = ""; // prevent null crash
        String statusCode = rawStatus.trim().toLowerCase();  // normalize input

        // Debug log (you can remove after confirming)
        Log.d("OrderStatusCheck", "Status from Firebase: '" + rawStatus + "' | Trimmed: '" + statusCode + "'");

        String statusText = "Unknown";
        int statusColor = R.color.gold;

        // Match status either by numeric code or status text
        switch (statusCode) {
            case "0":
            case "ordered":
            case "processing":
                statusText = "Ordered";
                statusColor = R.color.orange;
                break;
            case "1":
            case "shipped":
                statusText = "Shipped";
                statusColor = R.color.blue;
                break;
            case "2":
            case "out for delivery":
            case "out_for_delivery":
                statusText = "Out For Delivery";
                statusColor = R.color.red;
                break;
            case "3":
            case "delivered":
                statusText = "Delivered";
                statusColor = R.color.green;
                break;
        }

        holder.statusText.setText(statusText);
        holder.statusText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), statusColor));

        // Load product images
        List<String> images = order.getImageUrls();
        loadImage(holder.productImage1, images, 0);
        loadImage(holder.productImage2, images, 1);
        loadImage(holder.productImage3, images, 2);
        loadImage(holder.productImage4, images, 3);

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (onOrderClickListener != null) {
                onOrderClickListener.onOrderClick(order);
            }
        });
    }

    private void loadImage(ImageView view, List<String> images, int index) {
        if (view != null && images != null && index < images.size()) {
            Glide.with(view.getContext())
                    .load(images.get(index))
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(view);
        } else if (view != null) {
            view.setImageResource(R.drawable.placeholder_image);
        }
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView statusText, orderDetailsText, orderDate;
        ImageView productImage1, productImage2, productImage3, productImage4;
        RatingBar ratingBar;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            statusText = itemView.findViewById(R.id.orderStatusText);
            orderDetailsText = itemView.findViewById(R.id.orderAmount);
            orderDate = itemView.findViewById(R.id.orderdates);
            productImage1 = itemView.findViewById(R.id.productImage1);
            productImage2 = itemView.findViewById(R.id.productImage2);
            productImage3 = itemView.findViewById(R.id.productImage3);
            productImage4 = itemView.findViewById(R.id.productImage4);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}





