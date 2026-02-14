package com.empmgmt.controller;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final JdbcTemplate jdbc;

    public DashboardController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping
    @Cacheable("dashboard")
    public Map<String, Object> getDashboardStats() {
        // Single query to get all dashboard stats at once (avoids 5 round trips to PUB400)
        Map<String, Object> stats = jdbc.queryForMap(
            "SELECT " +
            "(SELECT COUNT(*) FROM NGIRI4001.EMPLOYEES) AS totalEmployees, " +
            "(SELECT COUNT(*) FROM NGIRI4001.EMPLOYEES WHERE EMPSTATUS = 'ACTIVE') AS activeEmployees, " +
            "(SELECT COUNT(*) FROM NGIRI4001.DEPARTMENTS) AS totalDepartments, " +
            "(SELECT COUNT(*) FROM NGIRI4001.POSITIONS) AS totalPositions, " +
            "(SELECT COUNT(*) FROM NGIRI4001.LEAVEREQS WHERE STATUS = 'PENDING') AS pendingLeaveRequests " +
            "FROM SYSIBM.SYSDUMMY1"
        );
        // Convert keys to camelCase (JDBC returns uppercase)
        Map<String, Object> result = new HashMap<>();
        result.put("totalEmployees", stats.get("TOTALEMPLOYEES"));
        result.put("activeEmployees", stats.get("ACTIVEEMPLOYEES"));
        result.put("totalDepartments", stats.get("TOTALDEPARTMENTS"));
        result.put("totalPositions", stats.get("TOTALPOSITIONS"));
        result.put("pendingLeaveRequests", stats.get("PENDINGLEAVEREQUESTS"));
        return result;
    }
}
