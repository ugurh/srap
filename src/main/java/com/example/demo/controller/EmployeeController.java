package com.example.demo.controller;

import com.example.demo.dtos.EmployeeDto;
import com.example.demo.mappers.EmployeeMapper;
import com.example.demo.model.Employee;
import com.example.demo.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
    private final EmployeeMapper employeeMapper;

    public EmployeeController(EmployeeService employeeService, EmployeeMapper employeeMapper) {
        this.employeeService = employeeService;
        this.employeeMapper = employeeMapper;
    }

    @GetMapping("/all")
    public ResponseEntity<List<EmployeeDto>> getAllGroups() {
        log.info("Request to get employees");
        List<Employee> employees = employeeService.getAll();

        return new ResponseEntity<>(employeeMapper.convertToDto(employees), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Cacheable(cacheNames = "employee", key = "'employee#' + #id", cacheManager = "redisCacheManager")
    public ResponseEntity<EmployeeDto> get(@PathVariable Long id) {
        log.info("Request to get employee: {}", id);
        Employee employee = employeeService.get(id).orElse(null);

        return new ResponseEntity<>(employeeMapper.convertToDto(employee), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<EmployeeDto> create(@Valid @RequestBody EmployeeDto employeeDto) throws URISyntaxException {
        log.info("Request to create employee: {}", employeeDto);
        Employee employee = employeeService.create(employeeMapper.convertToEntity(employeeDto));
        return ResponseEntity.created(new URI("/api/employee/" + employee.getId()))
                .body(employeeMapper.convertToDto(employee));
    }

    @PutMapping("/update/{id}")
    @CachePut(cacheNames = "employee", key = "'employee#' + #id", cacheManager = "redisCacheManager")
    public ResponseEntity<EmployeeDto> update(@PathVariable Long id, @Valid @RequestBody EmployeeDto employeeDto) {
        log.info("Request to update employee: {}", employeeDto);
        Employee employee = employeeService.update(employeeMapper.convertToEntity(employeeDto));
        return ResponseEntity.ok().body(employeeMapper.convertToDto(employee));
    }

    @DeleteMapping("/{id}")
    @CacheEvict(cacheNames = "employee", key = "'employee#' + #id", cacheManager = "redisCacheManager")
    public ResponseEntity<String> deleteGroup(@PathVariable Long id) {
        log.info("Request to delete employee: {}", id);
        employeeService.deleteById(id);
        return ResponseEntity.ok().body("Employee deleted.");
    }
}
