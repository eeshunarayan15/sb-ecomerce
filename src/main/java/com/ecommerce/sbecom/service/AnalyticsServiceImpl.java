package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.payload.AnalyticsResponse;
import com.ecommerce.sbecom.repository.OrderRepository;
import com.ecommerce.sbecom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService{
   private final OrderRepository orderRepository;
   private final ProductRepository productRepository;
    @Override
    public AnalyticsResponse getAnalyicsData() {
        long count = productRepository.count();
        long totalOrders=orderRepository.count();
        Double totalRevenue=orderRepository.getTotalRevenue();

        return AnalyticsResponse.builder()
                .totalRevenue(totalRevenue.toString())
                .productCounts(String.valueOf(count))
                .totalOrders(String.valueOf(totalOrders))
                .userCounts(String.valueOf(count))
                .build();

    }
}
