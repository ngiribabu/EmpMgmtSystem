package com.empmgmt.controller;

import com.empmgmt.model.Dependent;
import com.empmgmt.service.DependentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dependents")
public class DependentController {

    private final DependentService service;

    public DependentController(DependentService service) { this.service = service; }

    @GetMapping("/{id}")
    public Dependent getById(@PathVariable Integer id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Dependent create(@Valid @RequestBody Dependent dep) { return service.create(dep); }

    @PutMapping("/{id}")
    public Dependent update(@PathVariable Integer id, @Valid @RequestBody Dependent dep) {
        return service.update(id, dep);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
