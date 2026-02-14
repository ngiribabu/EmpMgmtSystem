package com.empmgmt.controller;

import com.empmgmt.model.EmpHist;
import com.empmgmt.service.EmpHistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
public class EmpHistController {

    private final EmpHistService service;

    public EmpHistController(EmpHistService service) { this.service = service; }

    @GetMapping("/{id}")
    public EmpHist getById(@PathVariable Integer id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpHist create(@Valid @RequestBody EmpHist hist) { return service.create(hist); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
