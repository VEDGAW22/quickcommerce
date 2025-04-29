package com.example.quickcommerce.models;

import java.io.Serializable;


    public class Cart implements Serializable {
        private Product product;
        private String selectedSize;

        public Cart(Product product, String selectedSize) {
            this.product = product;
            this.selectedSize = selectedSize;
        }

        public Product getProduct() {
            return product;
        }

        public String getSelectedSize() {
            return selectedSize;
        }

    }

