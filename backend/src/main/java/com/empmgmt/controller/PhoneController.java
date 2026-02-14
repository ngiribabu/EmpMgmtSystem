package com.empmgmt.controller;

import com.empmgmt.model.EmpPhoneNb;
import com.empmgmt.service.EmpPhoneNbService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/phones")
public class PhoneController {

    private final EmpPhoneNbService service;

    public PhoneController(EmpPhoneNbService service) { this.service = service; }

    @GetMapping("/{id}")
    public EmpPhoneNb getById(@PathVariable Integer id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpPhoneNb create(@Valid @RequestBody EmpPhoneNb phone) { return service.create(phone); }

    @PutMapping("/{id}")
    public EmpPhoneNb update(@PathVariable Integer id, @Valid @RequestBody EmpPhoneNb phone) {
        return service.update(id, phone);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
