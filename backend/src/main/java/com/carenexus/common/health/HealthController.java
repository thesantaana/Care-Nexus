package com.carenexus.common.health;

import com.carenexus.common.response.ApiResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public ApiResponse<Map<String, String>> health() {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("status", "UP");
        data.put("service", "care-nexus-backend");
        return ApiResponse.success(data);
    }
}
