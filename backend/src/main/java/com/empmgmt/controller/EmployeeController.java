package com.empmgmt.controller;

import com.empmgmt.model.*;
import com.empmgmt.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmpPhoneNbService phoneService;
    private final DependentService dependentService;
    private final SalaryService salaryService;
    private final EmpHistService histService;

    public EmployeeController(EmployeeService employeeService, EmpPhoneNbService phoneService,
                              DependentService dependentService, SalaryService salaryService,
                              EmpHistService histService) {
        this.employeeService = employeeService;
        this.phoneService = phoneService;
        this.dependentService = dependentService;
        this.salaryService = salaryService;
        this.histService = histService;
    }

    @GetMapping
    public List<Employee> getAll(@RequestParam(required = false) String search,
                                 @RequestParam(required = false) Integer deptId,
                                 @RequestParam(required = false) String status) {
        if (search != null && !search.isBlank()) return employeeService.search(search);
        if (deptId != null) return employeeService.findByDept(deptId);
        if (status != null) return employeeService.findByStatus(status);
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    public Employee getById(@PathVariable Integer id) { return employeeService.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employee create(@Valid @RequestBody Employee emp) { return employeeService.create(emp); }

    @PutMapping("/{id}")
    public Employee update(@PathVariable Integer id, @Valid @RequestBody Employee emp) {
        return employeeService.update(id, emp);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { employeeService.delete(id); }

    // Nested: Phones
    @GetMapping("/{id}/phones")
    public List<EmpPhoneNb> getPhones(@PathVariable Integer id) { return phoneService.findByEmployee(id); }

    // Nested: Dependents
    @GetMapping("/{id}/dependents")
    public List<Dependent> getDependents(@PathVariable Integer id) { return dependentService.findByEmployee(id); }

    // Nested: Salaries
    @GetMapping("/{id}/salaries")
    public List<Salary> getSalaries(@PathVariable Integer id) { return salaryService.findByEmployee(id); }

    // Nested: History
    @GetMapping("/{id}/history")
    public List<EmpHist> getHistory(@PathVariable Integer id) { return histService.findByEmployee(id); }
}
