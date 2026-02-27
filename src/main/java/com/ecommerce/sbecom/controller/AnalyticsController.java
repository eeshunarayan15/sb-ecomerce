package com.ecommerce.sbecom.controller;

import com.ecommerce.sbecom.payload.AnalyticsResponse;
import com.ecommerce.sbecom.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    @GetMapping
    public ResponseEntity<AnalyticsResponse> getAnalytics(){
        AnalyticsResponse analyicsData = analyticsService.getAnalyicsData();
        return ResponseEntity.ok(analyicsData);
    }
}
