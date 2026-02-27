package com.ecommerce.sbecom.payload;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsResponse {
    private String productCounts;
    private String totalOrders;
    private String userCounts;
    private String totalRevenue;
}
