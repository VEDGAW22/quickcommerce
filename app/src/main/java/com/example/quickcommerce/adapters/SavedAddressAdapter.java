package com.example.quickcommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcommerce.R;
import com.example.quickcommerce.models.AddressModel;

import java.util.List;

public class SavedAddressAdapter extends RecyclerView.Adapter<SavedAddressAdapter.AddressViewHolder> {

    private List<AddressModel> addressList; // Now using AddressModel
    private OnAddressDeleteListener onAddressDeleteListener;
    private int selectedPosition = -1; // Keep track of the selected item

    public SavedAddressAdapter(List<AddressModel> addressList, OnAddressDeleteListener onAddressDeleteListener) {
        this.addressList = addressList;
        this.onAddressDeleteListener = onAddressDeleteListener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        AddressModel address = addressList.get(position);

        holder.tvName.setText(address.getFullName());
        holder.tvAddress.setText(address.getAddress());
        holder.tvPhone.setText("Phone: " + address.getPhone());

        // Highlight if selected
        if (position == selectedPosition) {
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.selected_address_background));
        } else {
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.default_address_background));
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (onAddressDeleteListener != null) {
                onAddressDeleteListener.onAddressDeleted(position);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    // Get selected address
    public AddressModel getSelectedAddress() {
        if (selectedPosition != -1) {
            return addressList.get(selectedPosition);
        }
        return null;
    }

    public interface OnAddressDeleteListener {
        void onAddressDeleted(int position);
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvPhone;
        ImageView btnDelete;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}