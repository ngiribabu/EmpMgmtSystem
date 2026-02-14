package com.empmgmt.controller;

import com.empmgmt.model.Department;
import com.empmgmt.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService service;

    public DepartmentController(DepartmentService service) { this.service = service; }

    @GetMapping
    public List<Department> getAll() { return service.findAll(); }

    @GetMapping("/active")
    public List<Department> getActive() { return service.findActive(); }

    @GetMapping("/{id}")
    public Department getById(@PathVariable Integer id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Department create(@Valid @RequestBody Department dept) { return service.create(dept); }

    @PutMapping("/{id}")
    public Department update(@PathVariable Integer id, @Valid @RequestBody Department dept) {
        return service.update(id, dept);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
