package com.example.quickcommerce.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quickcommerce.databinding.CheckoutItemLayoutBinding;
import com.example.quickcommerce.models.Cart;

public class AdapterCart extends ListAdapter<Cart, AdapterCart.CartViewHolder> {
    private OnRemoveClickListener removeClickListener;

    // Interface for handling item removal click
    public interface OnRemoveClickListener {
        void onRemoveClick(int position);
    }

    // Constructor to initialize the remove click listener
    public AdapterCart(OnRemoveClickListener listener) {
        super(DIFF_CALLBACK);  // Pass DiffUtil.ItemCallback
        this.removeClickListener = listener;
    }

    // DiffUtil callback to compare items for efficient updates
    private static final DiffUtil.ItemCallback<Cart> DIFF_CALLBACK = new DiffUtil.ItemCallback<Cart>() {
        @Override
        public boolean areItemsTheSame(@NonNull Cart oldItem, @NonNull Cart newItem) {
            // Compare by unique identifier (e.g., product ID or cart item ID)
            return oldItem.getProduct().getId().equals(newItem.getProduct().getId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Cart oldItem, @NonNull Cart newItem) {
            // Compare the content of the items (product, size, etc.)
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CheckoutItemLayoutBinding binding = CheckoutItemLayoutBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart item = getItem(position); // Use getItem() to get the current item
        holder.bind(item);

        // Cross button click listener to remove item
        holder.binding.ivCross.setOnClickListener(v -> {
            if (removeClickListener != null) {
                removeClickListener.onRemoveClick(position);  // Notify listener for item removal
            }
        });
    }

    // ViewHolder for binding the cart item views
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        private final CheckoutItemLayoutBinding binding;

        public CartViewHolder(CheckoutItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Cart cart) {
            binding.productNameTextView.setText(cart.getProduct().getTitle());
            binding.tvSize.setText("Size: " + cart.getSelectedSize());
            binding.productPriceTextView.setText("₹" + cart.getProduct().getPrice());

            // Loading image using Glide
            Glide.with(binding.ivProductImage.getContext())
                    .load(cart.getProduct().getImageUrls().get(0))  // Load the first image URL
                    .into(binding.ivProductImage);
        }
    }
}
