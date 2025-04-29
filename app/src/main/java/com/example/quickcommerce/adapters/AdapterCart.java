package com.example.quickcommerce.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quickcommerce.databinding.CheckoutItemLayoutBinding;
import com.example.quickcommerce.models.Cart;

import java.util.List;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.CartViewHolder> {
    private List<Cart> cartItems;
    private OnRemoveClickListener removeClickListener;

    // Interface for handling item removal click
    public interface OnRemoveClickListener {
        void onRemoveClick(int position);
    }

    // Constructor to initialize cart items and remove click listener
    public AdapterCart(List<Cart> cartItems, OnRemoveClickListener listener) {
        this.cartItems = cartItems;
        this.removeClickListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CheckoutItemLayoutBinding binding = CheckoutItemLayoutBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart item = cartItems.get(position);
        holder.bind(item);

        // Cross button click listener to remove item
        holder.binding.ivCross.setOnClickListener(v -> {
            if (removeClickListener != null) {
                removeClickListener.onRemoveClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
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
