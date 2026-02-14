package com.empmgmt.controller;

import com.empmgmt.model.Salary;
import com.empmgmt.service.SalaryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/salaries")
public class SalaryController {

    private final SalaryService service;

    public SalaryController(SalaryService service) { this.service = service; }

    @GetMapping
    public List<Salary> getAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public Salary getById(@PathVariable Integer id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Salary create(@Valid @RequestBody Salary salary) { return service.create(salary); }

    @PutMapping("/{id}")
    public Salary update(@PathVariable Integer id, @Valid @RequestBody Salary salary) {
        return service.update(id, salary);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
