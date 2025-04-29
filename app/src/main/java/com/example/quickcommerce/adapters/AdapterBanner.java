package com.example.quickcommerce.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcommerce.R;

import java.util.ArrayList;

public class AdapterBanner extends RecyclerView.Adapter<AdapterBanner.BannerViewHolder> {

    private final ArrayList<Integer> banners;

    public AdapterBanner(ArrayList<Integer> banners) {
        this.banners = banners;
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.bannerImage);
        }
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        holder.image.setImageResource(banners.get(position));
    }

    @Override
    public int getItemCount() {
        return banners.size();
    }
}

