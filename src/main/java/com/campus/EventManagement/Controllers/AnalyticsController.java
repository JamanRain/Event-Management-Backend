package com.campus.EventManagement.Controllers;

import com.campus.EventManagement.Services.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics() {

        return ResponseEntity.ok(
                analyticsService.getAnalytics()
        );
    }
}
