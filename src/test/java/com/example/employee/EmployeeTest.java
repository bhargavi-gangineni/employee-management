package com.example.employee;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmployeeTest {

    @Test
    void shouldCreateEmployeeCorrectly() {

        Employee employee = new Employee(
                101,
                "Raviteja",
                "raviteja@example.com",
                "IT",
                50000
        );

        assertEquals(101, employee.getId());
        assertEquals("Raviteja", employee.getName());
        assertEquals("raviteja@example.com", employee.getEmail());
        assertEquals("IT", employee.getDepartment());
        assertEquals(50000, employee.getSalary());
    }
}