package com.example.employee;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmployeeTest {

    @Test
    void shouldCreateEmployeeCorrectly() {

        Employee employee = new Employee(
                101,
                "Ravi",
                "ravi@example.com",
                "IT",
                50000
        );

        assertEquals(101, employee.getId());
        assertEquals("Ravi", employee.getName());
        assertEquals("ravi@example.com", employee.getEmail());
        assertEquals("IT", employee.getDepartment());
        assertEquals(50000, employee.getSalary());
    }
}