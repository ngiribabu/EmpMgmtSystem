package com.empmgmt.controller;

import com.empmgmt.model.LeaveReq;
import com.empmgmt.service.LeaveReqService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveReqController {

    private final LeaveReqService service;

    public LeaveReqController(LeaveReqService service) { this.service = service; }

    @GetMapping
    public List<LeaveReq> getAll(@RequestParam(required = false) Integer empId,
                                 @RequestParam(required = false) String status) {
        if (empId != null) return service.findByEmployee(empId);
        if (status != null) return service.findByStatus(status);
        return service.findAll();
    }

    @GetMapping("/pending")
    public List<LeaveReq> getPending() { return service.findPending(); }

    @GetMapping("/{id}")
    public LeaveReq getById(@PathVariable Integer id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveReq create(@Valid @RequestBody LeaveReq req) { return service.create(req); }

    @PutMapping("/{id}")
    public LeaveReq update(@PathVariable Integer id, @Valid @RequestBody LeaveReq req) {
        return service.update(id, req);
    }

    @PutMapping("/{id}/approve")
    public LeaveReq approve(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        Integer approverId = (Integer) body.get("approverId");
        String comments = (String) body.get("comments");
        return service.approve(id, approverId, comments);
    }

    @PutMapping("/{id}/reject")
    public LeaveReq reject(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        Integer approverId = (Integer) body.get("approverId");
        String comments = (String) body.get("comments");
        return service.reject(id, approverId, comments);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
