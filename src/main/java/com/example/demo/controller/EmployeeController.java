package com.example.demo.controller;

import com.example.demo.model.Employee;
import com.example.demo.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Employee>> getAllGroups() {
        log.info("Request to get employees");
        List<Employee> groups = employeeService.getAll();
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> get(@PathVariable Long id) {
        log.info("Request to get employee: {}", id);
        Employee employee = employeeService.get(id).orElse(null);

        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody Employee employee) throws URISyntaxException {
        log.info("Request to create employee: {}", employee);
        Employee result = employeeService.create(employee);
        return ResponseEntity.created(new URI("/api/employee/" + result.getId()))
                .body(result);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody Employee employee) {
        log.info("Request to update employee: {}", employee);
        Employee result = employeeService.create(employee);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long id) {
        log.info("Request to delete employee: {}", id);
        employeeService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
