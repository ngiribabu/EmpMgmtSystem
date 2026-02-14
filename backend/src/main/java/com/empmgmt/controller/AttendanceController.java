package com.empmgmt.controller;

import com.empmgmt.model.Attendance;
import com.empmgmt.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService service;

    public AttendanceController(AttendanceService service) { this.service = service; }

    @GetMapping
    public List<Attendance> getAll(@RequestParam(required = false) Integer empId) {
        if (empId != null) return service.findByEmployee(empId);
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Attendance getById(@PathVariable Integer id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Attendance create(@Valid @RequestBody Attendance att) { return service.create(att); }

    @PutMapping("/{id}")
    public Attendance update(@PathVariable Integer id, @Valid @RequestBody Attendance att) {
        return service.update(id, att);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
