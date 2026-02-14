package com.empmgmt.controller;

import com.empmgmt.model.Position;
import com.empmgmt.service.PositionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/positions")
public class PositionController {

    private final PositionService service;

    public PositionController(PositionService service) { this.service = service; }

    @GetMapping
    public List<Position> getAll(@RequestParam(required = false) Integer deptId) {
        if (deptId != null) return service.findByDept(deptId);
        return service.findAll();
    }

    @GetMapping("/active")
    public List<Position> getActive() { return service.findActive(); }

    @GetMapping("/{id}")
    public Position getById(@PathVariable Integer id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Position create(@Valid @RequestBody Position pos) { return service.create(pos); }

    @PutMapping("/{id}")
    public Position update(@PathVariable Integer id, @Valid @RequestBody Position pos) {
        return service.update(id, pos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
