package com.empmgmt.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(
            "dashboard", "employees", "employeeById",
            "departments", "departmentById",
            "positions", "positionById",
            "phones", "salaries", "dependents", "empHist",
            "attendance", "leaveTypes", "leaveReqs"
        );
        manager.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)  // Cache for 10 minutes
            .maximumSize(500));
        return manager;
    }
}
