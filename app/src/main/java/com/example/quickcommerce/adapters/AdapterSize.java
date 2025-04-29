package com.example.quickcommerce.adapters;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcommerce.R;

import java.util.List;

public class AdapterSize extends RecyclerView.Adapter<AdapterSize.SizeViewHolder> {

    private final List<String> sizeList;
    private int selectedPosition = -1;
    private final OnSizeSelectedListener listener;

    public interface OnSizeSelectedListener {
        void onSizeSelected(String size);
    }

    public AdapterSize(List<String> sizeList, OnSizeSelectedListener listener) {
        this.sizeList = sizeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView sizeText = new TextView(parent.getContext());
        int padding = (int) (8 * parent.getContext().getResources().getDisplayMetrics().density);
        sizeText.setPadding(padding, padding, padding, padding);
        sizeText.setGravity(Gravity.CENTER);
        sizeText.setTextSize(16);
        sizeText.setLayoutParams(new ViewGroup.LayoutParams(160, 160));
        sizeText.setBackgroundResource(R.drawable.size_unselected_bg); // XML drawable
        return new SizeViewHolder(sizeText);
    }

    @Override
    public void onBindViewHolder(@NonNull SizeViewHolder holder, int position) {
        String size = sizeList.get(position);
        TextView textView = (TextView) holder.itemView;
        textView.setText(size);

        if (selectedPosition == position) {
            textView.setBackgroundResource(R.drawable.size_selected_bg);
            textView.setTextColor(Color.WHITE);
        } else {
            textView.setBackgroundResource(R.drawable.size_unselected_bg);
            textView.setTextColor(Color.BLACK);
        }

        textView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            listener.onSizeSelected(sizeList.get(selectedPosition));
        });
    }

    @Override
    public int getItemCount() {
        return sizeList.size();
    }

    static class SizeViewHolder extends RecyclerView.ViewHolder {
        public SizeViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
