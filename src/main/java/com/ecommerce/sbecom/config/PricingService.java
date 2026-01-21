package com.ecommerce.sbecom.config;

import com.ecommerce.sbecom.model.Product;
import org.springframework.stereotype.Service;

@Service
public class PricingService {

    public Double calculateLivePrice(Product product) {
        // Future Logic: Yahan check karein ki kya aaj koi sale active hai?
        // Ya user kisi special coupon ke liye eligible hai?
        
        if (product.getSpecialPrice() > 0 && product.getSpecialPrice() < product.getPrice()) {
            return product.getSpecialPrice();
        }
        return product.getPrice();
    }
}