package com.empmgmt.config;

import com.empmgmt.service.EmployeeService;
import com.empmgmt.service.DepartmentService;
import com.empmgmt.service.PositionService;
import com.empmgmt.service.AttendanceService;
import com.empmgmt.service.LeaveTypeService;
import com.empmgmt.service.LeaveReqService;
import com.empmgmt.service.SalaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Pre-loads all caches on application startup so the first visitor
 * gets instant responses without waiting for DB2 round-trips.
 */
@Component
public class CacheWarmup {

    private static final Logger log = LoggerFactory.getLogger(CacheWarmup.class);

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final PositionService positionService;
    private final AttendanceService attendanceService;
    private final LeaveTypeService leaveTypeService;
    private final LeaveReqService leaveReqService;
    private final SalaryService salaryService;

    public CacheWarmup(EmployeeService employeeService,
                       DepartmentService departmentService,
                       PositionService positionService,
                       AttendanceService attendanceService,
                       LeaveTypeService leaveTypeService,
                       LeaveReqService leaveReqService,
                       SalaryService salaryService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.positionService = positionService;
        this.attendanceService = attendanceService;
        this.leaveTypeService = leaveTypeService;
        this.leaveReqService = leaveReqService;
        this.salaryService = salaryService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warmUpCaches() {
        log.info("Warming up caches...");
        long start = System.currentTimeMillis();
        try {
            employeeService.findAll();
            departmentService.findAll();
            positionService.findAll();
            salaryService.findAll();
            attendanceService.findAll();
            leaveTypeService.findAll();
            leaveReqService.findAll();
            long elapsed = System.currentTimeMillis() - start;
            log.info("Cache warmup complete in {}ms", elapsed);
        } catch (Exception e) {
            log.warn("Cache warmup failed (app will still work, caches fill on first request): {}", e.getMessage());
        }
    }
}
