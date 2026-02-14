package com.empmgmt.controller;

import com.empmgmt.model.LeaveType;
import com.empmgmt.service.LeaveTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/leave-types")
public class LeaveTypeController {

    private final LeaveTypeService service;

    public LeaveTypeController(LeaveTypeService service) { this.service = service; }

    @GetMapping
    public List<LeaveType> getAll() { return service.findAll(); }

    @GetMapping("/active")
    public List<LeaveType> getActive() { return service.findActive(); }

    @GetMapping("/{id}")
    public LeaveType getById(@PathVariable Integer id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveType create(@Valid @RequestBody LeaveType lt) { return service.create(lt); }

    @PutMapping("/{id}")
    public LeaveType update(@PathVariable Integer id, @Valid @RequestBody LeaveType lt) {
        return service.update(id, lt);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
