package com.example.quickcommerce.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.quickcommerce.R;
import com.example.quickcommerce.databinding.ItemViewProductBinding;
import com.example.quickcommerce.models.Product;

import java.util.ArrayList;
import java.util.List;

public class AdapterProduct extends ListAdapter<Product, AdapterProduct.ProductViewHolder> implements Filterable {

    private final List<Product> fullProductList = new ArrayList<>();
    private final List<Product> filteredList = new ArrayList<>();

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private OnProductClickListener productClickListener;

    public AdapterProduct() {
        super(DIFF_CALLBACK);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.productClickListener = listener;
    }

    public void submitProductList(List<Product> products) {
        fullProductList.clear();
        filteredList.clear();
        if (products != null) {
            fullProductList.addAll(products);
            filteredList.addAll(products);
        }
        submitList(new ArrayList<>(filteredList));
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewProductBinding binding = ItemViewProductBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = getItem(position);
        holder.bind(product);

        holder.itemView.setOnClickListener(v -> {
            if (productClickListener != null) {
                productClickListener.onProductClick(product);
            }
        });
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ItemViewProductBinding binding;

        public ProductViewHolder(@NonNull ItemViewProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Product product) {
            List<String> imageUrls = product.getImageUrls();
            ArrayList<SlideModel> imageList = new ArrayList<>();

            if (imageUrls != null && !imageUrls.isEmpty()) {
                for (String url : imageUrls) {
                    imageList.add(new SlideModel(url, ScaleTypes.FIT));
                }
            } else {
                imageList.add(new SlideModel("https://example.com/placeholder.jpg", ScaleTypes.FIT));
            }

            binding.ivImageSlider.setImageList(imageList);
            binding.tvProductTitle.setText(product.getTitle() != null ? product.getTitle() : "No title available");
            binding.tvProductPrice.setText(binding.getRoot().getContext().getString(R.string.currency_symbol) + product.getPrice());
            binding.tvProductDescripition.setText(product.getDescription() != null ? product.getDescription() : "No description available");
        }
    }

    private static final DiffUtil.ItemCallback<Product> DIFF_CALLBACK = new DiffUtil.ItemCallback<Product>() {
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Product> resultList = new ArrayList<>();
                if (TextUtils.isEmpty(constraint)) {
                    resultList.addAll(fullProductList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Product product : fullProductList) {
                        if (product.getTitle() != null &&
                                product.getTitle().toLowerCase().contains(filterPattern)) {
                            resultList.add(product);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = resultList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList.clear();
                if (results.values != null) {
                    filteredList.addAll((List<Product>) results.values);
                }
                submitList(new ArrayList<>(filteredList)); // Update ListAdapter
            }
        };
    }
}


