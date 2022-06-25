package com.example.demo.mappers;

import com.example.demo.dtos.EmployeeDto;
import com.example.demo.model.Employee;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EmployeeMapper {

    public EmployeeDto convertToDto(Employee employee) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(employee, new TypeReference<>() {
        });
    }

    public Employee convertToEntity(EmployeeDto employeeDto) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(employeeDto, new TypeReference<>() {
        });
    }

    public List<EmployeeDto> convertToDto(List<Employee> employees) {
        List<EmployeeDto> dtos = new ArrayList<>();
        for (Employee employee : employees) {
            dtos.add(convertToDto(employee));
        }

        return dtos;
    }
}
